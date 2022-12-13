use builder::commands::git_command::GitCommand;
use builder::commands::ssh_command::SSHCommand;
use builder::commands::ssl_command::SSLCommand;
use builder::commands::Commands::{Git, SSH, SSL};
use builder::commands::{Commands, GenerateProcessCommand};
use builder::log::init_log4rs;
use builder::options::base_options::{BaseOptions, LinkType};
use builder::path_data::PathData;
use builder::process_command_extend::CommandExtend;
use builder::tools::{create_dir, remove_dir_contents};
use clap::Parser;
use flate2::read::GzDecoder;
use log::info;
use std::env;
use std::fs::File;
use std::path::{Path, PathBuf};
use tar::Archive;

#[derive(Parser)]
#[command(author, version, about, long_about = None)]
pub struct Cli {
    #[command(subcommand)]
    pub command: Commands,

    #[clap(short = 'd', default_value_t = std::env::current_dir().unwrap().as_path().display().to_string())]
    pub work_dir: String,
}

//noinspection DuplicatedCode
fn main() {
    init_log4rs();
    println!("{} {}", env::consts::OS, env::consts::ARCH);
    let cli = Cli::parse();
    let work_dir = PathBuf::from(&cli.work_dir);
    match cli.command {
        Git(options) => {
            info!("Git command: {:#?}", options);
            let path_data = PathData::git(options.base.clone(), work_dir);
            let mut command = GitCommand::new(options, path_data);
            prepare(&command.options.base, &command.path_data);
            build_and_install(&command, &command.path_data);
            if let LinkType::Both = command.options.base.link_type {
                command.options.base.shared = !command.options.base.shared;
                build_and_install(&command, &command.path_data);
            }
        }
        SSH(options) => {
            info!("SSH command: {:#?}", options);
            let path_data = PathData::ssh(options.base.clone(), work_dir);
            let mut command = SSHCommand::new(options, path_data);
            prepare(&command.options.base, &command.path_data);
            build_and_install(&command, &command.path_data);
            if let LinkType::Both = command.options.base.link_type {
                command.options.base.shared = !command.options.base.shared;
                build_and_install(&command, &command.path_data);
            }
        }
        SSL(options) => {
            info!("SSL command: {:#?}", options);
            let path_data = PathData::openssl(options.base.clone(), work_dir);
            let mut command = SSLCommand::new(options, path_data);
            prepare(&command.options.base, &command.path_data);
            build_and_install(&command, &command.path_data);
            if let LinkType::Both = command.options.base.link_type {
                command.options.base.shared = !command.options.base.shared;
                build_and_install(&command, &command.path_data);
            }
        }
    };
}

fn prepare(base_options: &BaseOptions, path_data: &PathData) {
    info!("Prepare project: {}", path_data.project_name);
    decompress(&path_data.source_code_bundle(), &path_data.source_code_dir());
    if base_options.clean && Path::new(&path_data.build_dir()).exists() {
        remove_dir_contents(&path_data.build_dir());
    }
    create_dir(&path_data.build_dir());
    if base_options.clean && Path::new(&path_data.install_dir()).exists() {
        remove_dir_contents(&path_data.install_dir());
    }
    create_dir(&path_data.install_dir());
}

fn build_and_install(factory: &impl GenerateProcessCommand, path_data: &PathData) {
    let mut build_command = factory.generate_build_command();
    info!("CMake {}: {:#?}", path_data.project_name, build_command);
    build_command.run().expect("CMake failed");
    let mut make_command = factory.generate_make_command();
    info!("Make {}: {:#?}", path_data.project_name, make_command);
    make_command.run().expect("Make failed");
    let mut install_command = factory.generate_install_command();
    info!("Install {}: {:#?}", path_data.project_name, install_command);
    install_command.run().expect("Install failed");
}

fn decompress<T: AsRef<Path>>(source: T, target: T) {
    if target.as_ref().exists() {
        return;
    }
    create_dir(&target);
    info!("Decompressing {} to {}", source.as_ref().display(), target.as_ref().display());
    let tar_gz = File::open(source).unwrap();
    let tar = GzDecoder::new(tar_gz);
    let mut archive = Archive::new(tar);
    archive.entries().unwrap().for_each(|e| {
        let mut e = e.unwrap();
        let path = {
            let p = e.path().unwrap();
            PathBuf::from(target.as_ref()).join(p.strip_prefix(p.components().next().unwrap()).unwrap())
        };
        e.unpack(path).unwrap();
    });
}

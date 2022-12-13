use std::process::Command;

use crate::commands::GenerateProcessCommand;
use crate::options::ssl_options::SSLOptions;
use crate::options::Arch;
use crate::path_data::PathData;
use crate::process_command_extend::CommandExtend;

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub struct SSLCommand {
    pub options: SSLOptions,
    pub path_data: PathData,
}

impl SSLCommand {
    pub fn new(options: SSLOptions, path_data: PathData) -> SSLCommand {
        SSLCommand { options, path_data }
    }
}

impl GenerateProcessCommand for SSLCommand {
    fn generate_build_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        let mut arg = format!("{}/Configure", self.path_data.source_code_dir());
        if let Arch::LinuxX64 = self.options.base.arch {
            if let Some(os) = &self.options.os {
                arg.push_str(format!(" {}", os).as_str());
            }
            arg.push_str(format!(" --cross-compile-prefix={}", self.options.cross_prefix).as_str())
        }
        if !self.options.base.debug {
            arg.push_str(" --release");
        }
        arg.push_str(format!(" --prefix={}", self.path_data.install_dir()).as_str());
        arg.push_str(format!(" --openssldir={}/{}", self.path_data.install_dir(), "ssl").as_str());
        if !self.options.base.shared {
            arg.push_str(" no-shared");
        }
        arg.push_str(" no-asm no-acvp-tests no-buildtest-c++ no-external-tests no-unit-test");
        command.arg(arg);
        command
    }

    fn generate_make_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.arg(format!(
            "make {} build_sw -j{}",
            match self.options.base.verbose.is_silent() {
                true => "-s",
                false => "",
            },
            num_cpus::get(),
        ));
        command
    }

    fn generate_install_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.arg(format!(
            "make {} install_sw -j{}",
            match self.options.base.verbose.is_silent() {
                true => "-s",
                false => "",
            },
            num_cpus::get(),
        ));
        command
    }
}

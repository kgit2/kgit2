use std::process::Command;

use crate::commands::GenerateProcessCommand;
use crate::options::git_options::GitOptions;
use crate::options::{BuildType, CMakeBool};
use crate::path_data::PathData;
use crate::process_command_extend::CommandExtend;

#[derive(Clone, Hash, PartialEq, Eq)]
pub struct GitCommand {
    pub options: GitOptions,
    pub path_data: PathData,
}

impl From<GitOptions> for GitCommand {
    fn from(options: GitOptions) -> Self {
        let path_data = PathData::git(options.base.clone());
        GitCommand { options, path_data }
    }
}

impl GitCommand {
    pub fn join_pkg_config_path(&self) -> String {
        let ssh_path_data = PathData::ssh(self.options.base.clone());
        match self.path_data.openssl_root() {
            Some(openssl_root) => [ssh_path_data.pkg_config_dir(), format!("{}/lib/pkgconfig", openssl_root)].join(":"),
            None => ssh_path_data.pkg_config_dir(),
        }
    }
}

impl GenerateProcessCommand for GitCommand {
    fn generate_build_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.env("PKG_CONFIG_PATH", self.join_pkg_config_path());
        let mut arg = format!(
            "cmake {} {} {} {} {} {} {} {} {} {} {} {} {}",
            format_args!("-DCMAKE_BUILD_TYPE={}", BuildType::from(&self.options.base)),
            format_args!("-G Ninja"),
            format_args!("-S {}", self.path_data.source_code_dir()),
            format_args!("-B {}", self.path_data.build_dir()),
            format_args!("-DCMAKE_INSTALL_PREFIX={}", self.path_data.install_dir()),
            format_args!("-DBUILD_SHARED_LIBS={}", CMakeBool::from(&self.options.base)),
            format_args!("-DUSE_BUNDLED_ZLIB={}", CMakeBool::from(self.options.zlib)),
            format_args!("-DBUILD_CLI={}", CMakeBool::from(self.options.cli)),
            format_args!("-DUSE_SSH=ON"),
            format_args!("-DBUILD_TESTS=OFF"),
            format_args!("-DBUILD_EXAMPLES=OFF"),
            format_args!("-DCMAKE_C_FLAGS=\"-Wno-error=deprecated-declarations -Wno-deprecated-declarations\""),
            format_args!("-Wno-dev"),
        );
        if self.options.base.debug {
            arg.push_str(
                format!(
                    "{} {} {}",
                    format_args!("-DDEBUG_POOL=ON"),
                    format_args!("-DDEBUG_POOL=ON"),
                    format_args!("-DEBUG_STRICT_OPEN=ON")
                )
                .as_str(),
            );
        }
        command.arg(arg);
        command
    }

    //noinspection DuplicatedCode
    fn generate_make_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.arg(format!(
            "cmake --build {} --target all -j {}",
            self.path_data.build_dir(),
            num_cpus::get(),
        ));
        command
    }

    fn generate_install_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.arg(format!("cmake --build {} --target install", self.path_data.build_dir()));
        command
    }
}

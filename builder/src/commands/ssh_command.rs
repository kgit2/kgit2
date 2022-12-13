use std::cell::{Cell, RefCell};
use std::ops::Deref;
use std::process::Command;
use std::rc::Rc;

use log::info;

use crate::commands::GenerateProcessCommand;
use crate::options::base_options::BaseOptions;
use crate::options::ssh_options::SSHOptions;
use crate::options::{BuildType, CMakeBool};
use crate::path_data::PathData;
use crate::process_command_extend::CommandExtend;

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub struct SSHCommand {
    pub options: SSHOptions,
    pub path_data: PathData,
}

impl From<SSHOptions> for SSHCommand {
    fn from(options: SSHOptions) -> Self {
        let path_data = PathData::ssh(options.base.clone());
        SSHCommand { options, path_data }
    }
}

impl GenerateProcessCommand for SSHCommand {
    fn generate_build_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        if let Some(openssl_root_dir) = self.path_data.openssl_root() {
            info!("Set $OPENSSL_ROOT_DIR to {}", openssl_root_dir);
            command.env("OPENSSL_ROOT_DIR", openssl_root_dir);
        }
        let mut arg = format!(
            "cmake {} {} {} {} {} {} {} {} {} {} {} {} {}",
            format_args!("-DCMAKE_BUILD_TYPE={}", BuildType::from(&self.options.base)),
            format_args!("-G Ninja"),
            format_args!("-S {}", self.path_data.source_code_dir()),
            format_args!("-B {}", self.path_data.build_dir()),
            format_args!("-DCMAKE_INSTALL_PREFIX={}", self.path_data.install_dir()),
            format_args!("-DBUILD_SHARED_LIBS={}", CMakeBool::from(&self.options.base)),
            format_args!("-DCRYPTO_BACKEND={}", &self.options.crypto.to_string()),
            format_args!("-DENABLE_ZLIB_COMPRESSION={}", CMakeBool::from(self.options.zlib)),
            format_args!("-DLINT=OFF"),
            format_args!("-DBUILD_EXAMPLES=OFF"),
            format_args!("-DBUILD_TESTING=OFF"),
            format_args!("-DCMAKE_C_FLAGS=\"-Wno-error=deprecated-declarations -Wno-deprecated-declarations\""),
            format_args!("-Wno-dev"),
        );
        if self.options.base.debug {
            arg.push_str(format!("{}", format_args!("-DENABLE_DEBUG_LOGGING"),).as_str());
        }
        command.arg(arg);
        command
    }

    //noinspection DuplicatedCode
    fn generate_make_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.arg(format!(
            "cmake --build {} --target libssh2 -j {}",
            self.path_data.build_dir(),
            num_cpus::get()
        ));
        command
    }

    fn generate_install_command(&self) -> Command {
        let mut command = Command::from_path_data(&self.path_data, self.options.base.verbose.is_silent());
        command.arg(format!("cmake --build {} --target install", self.path_data.build_dir()));
        command
    }
}

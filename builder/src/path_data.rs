use crate::options::base_options::BaseOptions;
use crate::options::Arch;
use crate::{GIT_BUNDLE_NAME, GIT_NAME, OPENSSL_BUNDLE_NAME, OPENSSL_NAME, SSH_BUNDLE_NAME, SSH_NAME};
use std::env;
use std::fmt::Debug;
use std::process::Command;

#[derive(Clone, Hash, PartialEq, Eq)]
pub struct PathData {
    pub project_name: &'static str,
    pub bundle_name: &'static str,
    pub base_options: BaseOptions,
}

impl PathData {
    pub fn git(options: BaseOptions) -> PathData {
        PathData {
            project_name: GIT_NAME,
            base_options: options,
            bundle_name: GIT_BUNDLE_NAME,
        }
    }

    pub fn ssh(options: BaseOptions) -> PathData {
        PathData {
            project_name: SSH_NAME,
            base_options: options,
            bundle_name: SSH_BUNDLE_NAME,
        }
    }

    pub fn openssl(options: BaseOptions) -> PathData {
        PathData {
            project_name: OPENSSL_NAME,
            base_options: options,
            bundle_name: OPENSSL_BUNDLE_NAME,
        }
    }

    pub fn pkg_config_dir(&self) -> String {
        format!("{}/lib/pkgconfig", self.install_dir())
    }
}

impl PathData {
    pub fn source_code_dir(&self) -> String {
        match &self.base_options.paths.temp_dir {
            Some(prefix) => format!("{}/{}", prefix, self.project_name),
            None => {
                let temp = env::current_dir().unwrap().join("temp").as_path().display().to_string();
                format!("{}/{}", temp, self.project_name)
            }
        }
    }

    pub fn source_code_bundle(&self) -> String {
        match &self.base_options.paths.source_code_bundle_path {
            Some(bundle_path) => bundle_path.to_string(),
            None => format!(
                "{}/{}",
                env::current_dir().unwrap().join("source-code").as_path().display(),
                self.bundle_name
            ),
        }
    }

    pub fn install_dir(&self) -> String {
        match &self.base_options.paths.install_prefix {
            Some(prefix) => format!("{}/{}", prefix, self.project_name),
            None => {
                format!(
                    "{}/{}",
                    env::current_dir()
                        .unwrap()
                        .join(self.base_options.arch.to_string())
                        .as_path()
                        .display(),
                    self.project_name
                )
            }
        }
    }

    pub fn build_dir(&self) -> String {
        format!("{}/build", self.source_code_dir())
    }

    pub fn openssl_root(&self) -> Option<String> {
        match self.base_options.arch {
            Arch::MacosArm64 | Arch::MacosX64 => Some(
                String::from_utf8(Command::new("brew").args(["--prefix", "openssl"]).output().unwrap().stdout)
                    .unwrap()
                    .trim()
                    .to_string(),
            ),
            Arch::LinuxX64 => None,
            Arch::MingwX64 => None,
        }
    }
}

impl Debug for PathData {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let mut debug_struct = f.debug_struct("GitCommand");
        debug_struct.field("build_dir", &self.build_dir());
        debug_struct.field("install_dir", &self.install_dir());
        debug_struct.field("source_code_dir", &self.source_code_dir());
        debug_struct.field("source_code_bundle", &self.source_code_bundle());
        debug_struct.finish()
    }
}

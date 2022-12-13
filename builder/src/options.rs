extern crate proc_macro;

use std::fmt::{Display, Formatter};
use std::hash::Hash;

pub mod base_options;
pub mod clean_options;
pub mod git_options;
pub mod path_options;
pub mod ssh_options;
pub mod ssl_options;
use crate::options::base_options::BaseOptions;
use clap::ValueEnum;

#[derive(Copy, Clone, Debug, Hash, PartialEq, Eq, ValueEnum)]
pub enum Arch {
    #[clap(name = "mac_arm64")]
    MacosArm64,
    #[clap(name = "mac_x64")]
    MacosX64,
    #[clap(name = "linux_x64")]
    LinuxX64,
    #[clap(name = "mingw_x64")]
    MingwX64,
}

impl Display for Arch {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            Arch::MacosArm64 => write!(f, "macosArm64"),
            Arch::MacosX64 => write!(f, "macosX64"),
            Arch::LinuxX64 => write!(f, "linuxX64"),
            Arch::MingwX64 => write!(f, "mingwX64"),
        }
    }
}

impl Default for Arch {
    fn default() -> Self {
        Arch::MacosArm64
    }
}

#[derive(Copy, Clone, Debug, Hash, PartialEq, Eq, ValueEnum)]
pub enum CMakeBool {
    #[clap(name = "ON")]
    ON,
    #[clap(name = "OFF")]
    OFF,
}

impl Display for CMakeBool {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            CMakeBool::ON => write!(f, "ON"),
            CMakeBool::OFF => write!(f, "OFF"),
        }
    }
}

impl From<&BaseOptions> for CMakeBool {
    fn from(options: &BaseOptions) -> Self {
        Self::from(options.shared)
    }
}

impl From<bool> for CMakeBool {
    fn from(value: bool) -> Self {
        match value {
            true => CMakeBool::ON,
            false => CMakeBool::OFF,
        }
    }
}

#[derive(Copy, Clone, Debug, Hash, PartialEq, Eq, ValueEnum)]
pub enum BuildType {
    #[clap(name = "Debug")]
    Debug,
    #[clap(name = "Release")]
    Release,
}

impl Display for BuildType {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            BuildType::Debug => write!(f, "Debug"),
            BuildType::Release => write!(f, "Release"),
        }
    }
}

impl From<&BaseOptions> for BuildType {
    fn from(options: &BaseOptions) -> Self {
        match options.debug {
            true => BuildType::Debug,
            false => BuildType::Release,
        }
    }
}

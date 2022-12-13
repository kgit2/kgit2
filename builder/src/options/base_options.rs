use std::hash::{Hash, Hasher};

use clap::{Args, ValueEnum};
use clap_verbosity_flag::Verbosity;

use crate::options::path_options::PathOptions;
use crate::options::Arch;

#[derive(Clone, Debug, Args)]
pub struct BaseOptions {
    /// Build for target arch
    #[arg(default_value = "mac_arm64")]
    pub arch: Arch,

    /// Build shared libraries
    #[arg(short = 'S', long)]
    pub shared: bool,

    #[clap(default_value = "static")]
    pub link_type: LinkType,

    /// Build with debug type
    #[arg(short = 'D', long)]
    pub debug: bool,

    #[clap(flatten)]
    pub paths: PathOptions,

    #[clap(long, default_value_t = false)]
    pub clean: bool,

    #[clap(flatten)]
    pub verbose: Verbosity,
}

#[derive(Clone, Debug, Hash, PartialEq, Eq, ValueEnum)]
pub enum LinkType {
    #[clap(name = "static")]
    Static,
    #[clap(name = "shared")]
    Shared,
    #[clap(name = "both")]
    Both,
}

impl Default for LinkType {
    fn default() -> Self {
        LinkType::Static
    }
}

impl Hash for BaseOptions {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.arch.hash(state);
        self.shared.hash(state);
        self.debug.hash(state);
        self.paths.hash(state);
    }
}

impl PartialEq for BaseOptions {
    fn eq(&self, other: &Self) -> bool {
        self.arch == other.arch && self.shared == other.shared && self.debug == other.debug && self.paths == other.paths
    }
}

impl Eq for BaseOptions {}

impl Default for BaseOptions {
    fn default() -> Self {
        Self {
            arch: Arch::default(),
            shared: false,
            link_type: LinkType::Static,
            debug: false,
            paths: PathOptions::default(),
            clean: false,
            verbose: Verbosity::new(0, 0),
        }
    }
}

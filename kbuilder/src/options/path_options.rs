use clap::Args;
use std::fmt::Debug;

#[derive(Clone, Debug, Hash, PartialEq, Eq, Default, Args)]
pub struct PathOptions {
    /// Path to libs/temp
    pub temp_dir: Option<String>,
    /// Path to libs/arch/usr
    pub install_prefix: Option<String>,
    /// Path to libs/source-code
    pub source_code_bundle_path: Option<String>,
}

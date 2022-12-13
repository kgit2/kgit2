use crate::options::base_options::BaseOptions;
use clap::Args;

#[derive(Clone, Debug, Hash, PartialEq, Eq, Default, Args)]
pub struct GitOptions {
    #[clap(flatten)]
    pub base: BaseOptions,

    /// Build with bundled zlib
    #[clap(long)]
    pub zlib: bool,

    /// Build with git2_cli
    #[clap(long, default_value_t = true)]
    pub cli: bool,
}

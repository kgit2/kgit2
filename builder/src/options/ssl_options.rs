use crate::options::base_options::BaseOptions;
use clap::Args;

#[derive(Clone, Debug, Hash, PartialEq, Eq, Default, Args)]
pub struct SSLOptions {
    #[clap(flatten)]
    pub base: BaseOptions,
}

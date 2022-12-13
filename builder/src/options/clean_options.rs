use clap::ValueEnum;

#[derive(Copy, Clone, Debug, Hash, PartialEq, Eq, ValueEnum)]
pub enum CleanOptions {
    /// Clean the build directory
    #[clap(name = "build")]
    Build,

    /// Clean the install directory
    #[clap(name = "install")]
    Install,

    /// Clean the build and install directories
    #[clap(name = "all")]
    All,
}

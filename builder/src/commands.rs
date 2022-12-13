use std::process::Command;

use clap::Subcommand;

use crate::options::git_options::GitOptions;
use crate::options::ssh_options::SSHOptions;
use crate::options::ssl_options::SSLOptions;

pub mod git_command;
pub mod ssh_command;
pub mod ssl_command;

#[derive(Subcommand, Debug)]
pub enum Commands {
    /// Build libgit2
    // #[clap(subcommand)]
    Git(GitOptions),

    /// Build libssh2
    SSH(SSHOptions),

    /// Build openssl
    SSL(SSLOptions),
}

pub trait GenerateProcessCommand {
    fn generate_build_command(&self) -> Command;

    fn generate_make_command(&self) -> Command;

    fn generate_install_command(&self) -> Command;
}

// #![warn(missing_docs)]

pub mod commands;
pub mod log;
pub mod options;
pub mod path_data;
pub mod process_command_extend;
pub mod tools;

static GIT_NAME: &str = "libgit2";
static GIT_BUNDLE_NAME: &str = "libgit2-1.5.0.tar.gz";

static SSH_NAME: &str = "libssh2";
static SSH_BUNDLE_NAME: &str = "libssh2-1.10.0.tar.gz";

static OPENSSL_NAME: &str = "openssl";
static OPENSSL_BUNDLE_NAME: &str = "openssl-3.0.7.tar.gz";

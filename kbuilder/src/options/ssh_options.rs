use crate::options::base_options::BaseOptions;
use clap::{Args, ValueEnum};
use std::fmt::{Display, Formatter};

#[derive(Clone, Debug, Hash, PartialEq, Eq, Args)]
pub struct SSHOptions {
    #[clap(flatten)]
    pub base: BaseOptions,

    #[clap(long, default_value = "true")]
    pub zlib: bool,

    #[clap(long, default_value = "openssl")]
    pub crypto: CryptoBackend,
}

impl Default for SSHOptions {
    fn default() -> Self {
        SSHOptions {
            base: BaseOptions::default(),
            zlib: true,
            crypto: CryptoBackend::OpenSSL,
        }
    }
}

#[derive(Copy, Clone, Debug, Hash, PartialEq, Eq, ValueEnum)]
pub enum CryptoBackend {
    #[clap(name = "openssl")]
    OpenSSL,
    #[clap(name = "gnu")]
    GNU,
    #[clap(name = "win")]
    WinCNG,
    #[clap(name = "tls")]
    TLS,
    #[clap(name = "auto")]
    Auto,
}

impl Display for CryptoBackend {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            CryptoBackend::OpenSSL => write!(f, "OpenSSL"),
            CryptoBackend::GNU => write!(f, "Libgcrypt"),
            CryptoBackend::WinCNG => write!(f, "WinCNG"),
            CryptoBackend::TLS => write!(f, "mbedTLS"),
            CryptoBackend::Auto => write!(f, "Auto"),
        }
    }
}

impl Default for CryptoBackend {
    fn default() -> Self {
        CryptoBackend::OpenSSL
    }
}

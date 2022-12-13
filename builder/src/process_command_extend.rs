use crate::path_data::{ PathData};
use anyhow::{anyhow, Result};
use log::{info, warn};
use std::env;
use std::process::Command;

pub trait CommandExtend {
    fn from_path_data(path_data: &PathData, silent: bool) -> Self;

    fn run(&mut self) -> Result<()>;

    fn result(&mut self) -> Result<String>;

    fn pretty_print(&self);

    fn get_pkg_config<T: AsRef<str>>(pkg_config_path: Option<&str>, args: T) -> Result<String>;
}

impl CommandExtend for Command {
    fn from_path_data(path_data: &PathData, silent: bool) -> Command {
        let mut command = Command::new("sh");
        command.arg("-c");
        command.current_dir(path_data.build_dir());
        if silent {
            command.stdout(std::process::Stdio::null());
            command.stderr(std::process::Stdio::null());
        }
        env::vars().for_each(|e| {
            command.env(e.0, e.1);
        });
        command
    }

    fn run(&mut self) -> Result<()> {
        match self.spawn()?.wait()?.code().ok_or_else(|| anyhow!("No exit code"))? {
            0 => Ok(()),
            code => Err(anyhow!("Command failed with exit code: {}", code)),
        }
    }

    fn result(&mut self) -> Result<String> {
        let output = self.stdout(std::process::Stdio::piped()).spawn()?.wait_with_output()?;
        match output.status.code().ok_or_else(|| anyhow!("No exit code"))? {
            0 => Ok(String::from_utf8(output.stdout)?.trim().to_string()),
            code => Err(anyhow!("Command failed with exit code: {}", code)),
        }
    }

    fn pretty_print(&self) {
        let mut command_str = String::new();
        // print build_command's env
        for (key, value) in self.get_envs() {
            warn!("{}={}", key.to_str().unwrap(), value.unwrap().to_str().unwrap());
        }
        self.get_args().for_each(|arg| {
            command_str.push(' ');
            command_str.push_str(arg.to_str().unwrap());
        });
        info!("{}", command_str);
    }

    fn get_pkg_config<T: AsRef<str>>(pkg_config_path: Option<&str>, args: T) -> Result<String> {
        info!(
            "get pkg-config: PKG_CONFIG_PATH={} pkg-config {}",
            pkg_config_path.unwrap_or(""),
            args.as_ref()
        );
        Command::new("sh")
            .arg("-c")
            .env("PKG_CONFIG_PATH", pkg_config_path.unwrap_or(""))
            .arg(format!("pkg-config {}", args.as_ref()))
            .result()
    }
}

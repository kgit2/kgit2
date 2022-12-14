use std::path::Path;

use log::info;

pub fn create_dir<T: AsRef<Path>>(path: T) {
    if !path.as_ref().exists() {
        info!("Create directory: {}", path.as_ref().display());
        std::fs::create_dir_all(path).unwrap();
    }
}

pub fn remove_dir<T: AsRef<Path>>(path: T) {
    if path.as_ref().exists() {
        info!("Remove directory: {}", path.as_ref().display());
        std::fs::remove_dir_all(path).unwrap();
    }
}

pub fn remove_dir_contents<T: AsRef<Path>>(path: T) {
    if path.as_ref().exists() {
        info!("Remove directory contents: {}", path.as_ref().display());
        for entry in std::fs::read_dir(path).unwrap() {
            let path = entry.unwrap().path();
            if path.is_dir() {
                std::fs::remove_dir_all(path).unwrap();
            } else {
                std::fs::remove_file(path).unwrap();
            }
        }
    }
}

use std::fmt::Debug;
use std::fs::File;
use std::io::{BufRead, BufReader, Read};
use std::process::{Command, Stdio};

fn main() {
    let list_tests = Command::new("sh")
        .args([
            "-c",
            "../build/bin/native/debugTest/test.kexe --ktest_list_tests",
        ])
        .output()
        .unwrap()
        .stdout;
    let mut prefix: String = String::new();
    let mut test_list: Vec<String> = vec![];
    list_tests
        .lines()
        .into_iter()
        .map(|line| line.unwrap())
        .for_each(|line| {
            if line.starts_with("com.kgit2") {
                prefix = line;
            } else {
                test_list.push(format!("{}{}", prefix, line.trim()));
            }
        });
    test_list.iter().enumerate().for_each(|(i, test)| {
        let test = format!(
            "leaks --atExit -- ../build/bin/native/debugTest/test.kexe --ktest_filter={} > result.txt",
            test,
        );
        Command::new("sh").arg("-c").arg(&test).stderr(Stdio::null()).spawn().unwrap().wait().unwrap();
        let mut output = String::new();
        File::open("result.txt").unwrap().read_to_string(&mut output).unwrap();
        println!("{}:{}", &test, output.trim().lines().last().unwrap());
    });
}

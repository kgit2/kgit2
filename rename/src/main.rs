extern crate glob;

extern "C" {
    fn tree_sitter_kotlin() -> Language;
}

use std::fmt::{Display, Formatter, Write};
use std::io::Error;
use std::pin::Pin;
use std::task::{Context, Poll};

use colored::Colorize;
use convert_case::Case::Camel;
use convert_case::{Case, Casing};
use glob::glob;
use regex::Regex;
use tokio::fs::File;
use tokio::io::{AsyncReadExt, Result};
use tokio::io::{AsyncWrite, AsyncWriteExt};
use tree_sitter::{Language, Node, Parser, TreeCursor};

// #[tokio::main]
// async fn main() -> Result<()> {
//     let files = glob("../src/**/*.kt").unwrap();
//     for file in files.take(1) {
//         match file {
//             Ok(path) => {
//                 let mut document = Document {
//                     path: path.to_str().unwrap().to_string(),
//                     diffs: vec![],
//                 };
//                 println!("{:?}", path.canonicalize().unwrap());
//                 let mut file = File::open(path).await?;
//                 let mut buffer = String::new();
//                 file.read_to_string(&mut buffer).await?;
//                 let regex = Regex::new(r"va[rl] [a-z]+[A-Z][[:alpha:]]*:").unwrap();
//                 buffer.split('\n').enumerate().for_each(|(i, line)| {
//                     let mut diff = Diff {
//                         line: i,
//                         from: line.to_string(),
//                         to: None,
//                     };
//                     regex.find_iter(line).for_each(|m| {
//                         if diff.to.is_none() {
//                             diff.to = Some(diff.from.clone());
//                         }
//                         diff.to = Some(
//                             diff.to.take().unwrap().replace(
//                                 m.as_str(),
//                                 m.as_str()
//                                     .from_case(Case::UpperCamel)
//                                     .to_case(Case::Snake)
//                                     .as_str(),
//                             ),
//                         );
//                     });
//                     document.diffs.push(diff);
//                 });
//                 println!("{}", document);
//             }
//             Err(e) => println!("{:?}", e),
//         }
//     }
//     println!("Hello, world!");
//     Ok(())
// }
//
// struct Diff {
//     line: usize,
//     pub from: String,
//     pub to: Option<String>,
// }
//
// impl Display for Diff {
//     fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
//         match &self.to {
//             Some(to) => f.write_str(
//                 format!(
//                     "{}\n{}",
//                     format!("{}:-{}", self.line, self.from)
//                         .red()
//                         .to_string()
//                         .as_str(),
//                     format!("{}:+{}", self.line, to)
//                         .green()
//                         .to_string()
//                         .as_str()
//                 )
//                 .as_str(),
//             ),
//
//             None => write!(f, "{}:{}", self.line, self.from),
//         }
//     }
// }
//
// struct Document {
//     path: String,
//     diffs: Vec<Diff>,
// }
//
// impl Display for Document {
//     fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
//         for diff in &self.diffs {
//             writeln!(f, "{}", diff).unwrap();
//         }
//         Ok(())
//     }
// }
//
// impl Document {
//     fn to_code(&self) -> String {
//         let mut code = String::new();
//         for diff in &self.diffs {
//             match &diff.to {
//                 Some(to) => code.push_str(to),
//                 None => code.push_str(&diff.from),
//             }
//             code.push('\n');
//         }
//         code
//     }
// }

#[tokio::main]
async fn main() -> Result<()> {
    let mut parser = Parser::new();
    let language = unsafe { tree_sitter_kotlin() };
    parser.set_language(language).unwrap();
    let paths = glob("../src/**/*.kt").unwrap();
    for path in paths.take(1) {
        let path = path.unwrap();
        let mut source_file = File::open(path).await?;
        let mut source_code = String::new();
        source_file.read_to_string(&mut source_code).await?;
        let tree = parser.parse(source_code, None).unwrap();
        let root_node = tree.root_node();
        visit_source_file(root_node);
    }
    Ok(())
}

fn visit_source_file(root_node: Node) {
    for i in 0..root_node.named_child_count() {
        let node = root_node.named_child(i).unwrap();
        println!("{:?} {:?}", root_node.field_name_for_child(i as u32), node);
        match node.kind() {
            "package_header" | "import_header" => continue,
            "object_declaration" => visit_object_declaration(node),
            _ => (),
        }
    }
}

fn visit_object_declaration(parent_node: Node) {
    for i in 0..parent_node.named_child_count() {
        let child_node = parent_node.named_child(i).unwrap();
        println!(
            "{:?} {:?}",
            parent_node.field_name_for_child(i as u32),
            child_node
        );
    }
}

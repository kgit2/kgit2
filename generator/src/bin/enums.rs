use cli_clipboard::{ClipboardContext, ClipboardProvider};
use convert_case::{Case, Casing};
use std::env;
use std::fmt::{Display, Formatter};
use std::io::{read_to_string, stdin};

struct Enum {
    pub name: String,
    pub comment: String,
    pub prefix: String,
}

impl Enum {
    fn k_name(&self) -> String {
        self.name
            .replace(&self.prefix, "")
            .to_case(Case::UpperCamel)
    }
}

impl Display for Enum {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        writeln!(
            f,
            "\
/**
 * {}
 */
 {}({}),",
            self.comment,
            self.k_name(),
            self.name
        )
    }
}

fn main() {
    let args = env::args().collect::<Vec<String>>();
    if args.len() != 2 {
        println!("Usage: enums [prefix]");
        panic!("args error");
    }
    let prefix = args[1].to_string();
    let content = read_to_string(stdin()).unwrap();
    let mut flag: Option<Enum> = None;
    let mut enums: Vec<Enum> = vec![];
    content.lines().for_each(|line| {
        if line.starts_with(prefix.as_str()) {
            if let Some(f) = flag.take() {
                enums.push(f);
            }
            flag = Some(Enum {
                name: line.to_string(),
                comment: String::new(),
                prefix: prefix.clone(),
            })
        } else if let Some(f) = &mut flag {
            f.comment.push_str(line);
        }
    });
    if let Some(f) = flag.take() {
        enums.push(f);
    }
    let mut code = String::new();
    enums.iter().for_each(|f| {
        code.push_str(&format!("{}\n", f));
    });
    let mut ctx = ClipboardContext::new().unwrap();
    ctx.set_contents(code).unwrap();
}

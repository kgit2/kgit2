use cli_clipboard::{ClipboardContext, ClipboardProvider};
use std::env;
use std::fmt::{Display, Formatter};
use std::io::{read_to_string, stdin};

struct Flag {
    pub flag: String,
    pub comment: String,
}

impl Display for Flag {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        writeln!(
            f,
            "\
/**
 * {}
 */
 \"{}\",",
            self.comment, self.flag
        )
    }
}

fn main() {
    let args = env::args().collect::<Vec<String>>();
    if args.len() != 2 {
        println!("Usage: flags [prefix]");
        panic!("args error");
    }
    let prefix = &args[1];
    let content = read_to_string(stdin()).unwrap();
    let mut flag: Option<Flag> = None;
    let mut flags: Vec<Flag> = vec![];
    content.lines().for_each(|line| {
        if line.starts_with(prefix) {
            if let Some(f) = flag.take() {
                flags.push(f);
            }
            flag = Some(Flag {
                flag: line.to_string(),
                comment: String::new(),
            })
        } else if let Some(f) = &mut flag {
            f.comment.push_str(line);
        }
    });
    if let Some(f) = flag.take() {
        flags.push(f);
    }
    let mut code = String::new();
    flags.iter().for_each(|f| {
        code.push_str(&format!("{}\n", f));
    });
    let mut ctx = ClipboardContext::new().unwrap();
    ctx.set_contents(code).unwrap();
}

use std::fmt::{Display, Formatter};

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
 {},",
            self.comment, self.flag
        )
    }
}

fn main() {
    let content = "\
GIT_STASH_DEFAULT
No option, default

GIT_STASH_KEEP_INDEX
All changes already added to the index are left intact in the working directory

GIT_STASH_INCLUDE_UNTRACKED
All untracked files are also stashed and then cleaned up from the working directory

GIT_STASH_INCLUDE_IGNORED
All ignored files are also stashed and then cleaned up from the working directory
"
    .trim();
    let mut flag: Option<Flag> = None;
    let mut flags: Vec<Flag> = vec![];
    content.lines().for_each(|line| {
        if line.starts_with("GIT_STASH_") {
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
    flags.iter().for_each(|f| {
        println!("{}", f);
    })
}

use convert_case::{Case, Casing};
use std::fmt::{Display, Formatter};

static PREFIX: &str = "GIT_STASH_";

struct Flag {
    pub flag: String,
    pub comment: String,
}

impl Flag {
    fn k_name(&self) -> String {
        self.flag.replace(PREFIX, "").to_case(Case::UpperCamel)
    }
}

impl Display for Flag {
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
            self.flag
        )
    }
}

fn main() {
    let content = "\
GIT_STASH_APPLY_PROGRESS_NONE
GIT_STASH_APPLY_PROGRESS_LOADING_STASH
Loading the stashed data from the object database.

GIT_STASH_APPLY_PROGRESS_ANALYZE_INDEX
The stored index is being analyzed.

GIT_STASH_APPLY_PROGRESS_ANALYZE_MODIFIED
The modified files are being analyzed.

GIT_STASH_APPLY_PROGRESS_ANALYZE_UNTRACKED
The untracked and ignored files are being analyzed.

GIT_STASH_APPLY_PROGRESS_CHECKOUT_UNTRACKED
The untracked files are being written to disk.

GIT_STASH_APPLY_PROGRESS_CHECKOUT_MODIFIED
The modified files are being written to disk.

GIT_STASH_APPLY_PROGRESS_DONE
The stash was applied successfully.
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

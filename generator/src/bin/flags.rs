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
 \"{}\",",
            self.comment, self.flag
        )
    }
}

fn main() {
    let content = "\
GIT_DIFF_NORMAL
Normal diff, the default

GIT_DIFF_REVERSE
Reverse the sides of the diff

GIT_DIFF_INCLUDE_IGNORED
Include ignored files in the diff

GIT_DIFF_RECURSE_IGNORED_DIRS
Even with GIT_DIFF_INCLUDE_IGNORED, an entire ignored directory will be marked with only a single entry in the diff; this flag adds all files under the directory as IGNORED entries, too.

GIT_DIFF_INCLUDE_UNTRACKED
Include untracked files in the diff

GIT_DIFF_RECURSE_UNTRACKED_DIRS
Even with GIT_DIFF_INCLUDE_UNTRACKED, an entire untracked directory will be marked with only a single entry in the diff (a la what core Git does in git status); this flag adds all files under untracked directories as UNTRACKED entries, too.

GIT_DIFF_INCLUDE_UNMODIFIED
Include unmodified files in the diff

GIT_DIFF_INCLUDE_TYPECHANGE
Normally, a type change between files will be converted into a DELETED record for the old and an ADDED record for the new; this options enabled the generation of TYPECHANGE delta records.

GIT_DIFF_INCLUDE_TYPECHANGE_TREES
Even with GIT_DIFF_INCLUDE_TYPECHANGE, blob->tree changes still generally show as a DELETED blob. This flag tries to correctly label blob->tree transitions as TYPECHANGE records with new_file's mode set to tree. Note: the tree SHA will not be available.

GIT_DIFF_IGNORE_FILEMODE
Ignore file mode changes

GIT_DIFF_IGNORE_SUBMODULES
Treat all submodules as unmodified

GIT_DIFF_IGNORE_CASE
Use case insensitive filename comparisons

GIT_DIFF_INCLUDE_CASECHANGE
May be combined with GIT_DIFF_IGNORE_CASE to specify that a file that has changed case will be returned as an add/delete pair.

GIT_DIFF_DISABLE_PATHSPEC_MATCH
If the pathspec is set in the diff options, this flags indicates that the paths will be treated as literal paths instead of fnmatch patterns. Each path in the list must either be a full path to a file or a directory. (A trailing slash indicates that the path will only match a directory). If a directory is specified, all children will be included.

GIT_DIFF_SKIP_BINARY_CHECK
Disable updating of the binary flag in delta records. This is useful when iterating over a diff if you don't need hunk and data callbacks and want to avoid having to load file completely.

GIT_DIFF_ENABLE_FAST_UNTRACKED_DIRS
When diff finds an untracked directory, to match the behavior of core Git, it scans the contents for IGNORED and UNTRACKED files. If all contents are IGNORED, then the directory is IGNORED; if any contents are not IGNORED, then the directory is UNTRACKED. This is extra work that may not matter in many cases. This flag turns off that scan and immediately labels an untracked directory as UNTRACKED (changing the behavior to not match core Git).

GIT_DIFF_UPDATE_INDEX
When diff finds a file in the working directory with stat information different from the index, but the OID ends up being the same, write the correct stat information into the index. Note: without this flag, diff will always leave the index untouched.

GIT_DIFF_INCLUDE_UNREADABLE
Include unreadable files in the diff

GIT_DIFF_INCLUDE_UNREADABLE_AS_UNTRACKED
Include unreadable files in the diff

GIT_DIFF_INDENT_HEURISTIC
Use a heuristic that takes indentation and whitespace into account which generally can produce better diffs when dealing with ambiguous diff hunks.

GIT_DIFF_IGNORE_BLANK_LINES
Ignore blank lines

GIT_DIFF_FORCE_TEXT
Treat all files as text, disabling binary attributes & detection

GIT_DIFF_FORCE_BINARY
Treat all files as binary, disabling text diffs

GIT_DIFF_IGNORE_WHITESPACE
Ignore all whitespace

GIT_DIFF_IGNORE_WHITESPACE_CHANGE
Ignore changes in amount of whitespace

GIT_DIFF_IGNORE_WHITESPACE_EOL
Ignore whitespace at end of line

GIT_DIFF_SHOW_UNTRACKED_CONTENT
When generating patch text, include the content of untracked files. This automatically turns on GIT_DIFF_INCLUDE_UNTRACKED but it does not turn on GIT_DIFF_RECURSE_UNTRACKED_DIRS. Add that flag if you want the content of every single UNTRACKED file.

GIT_DIFF_SHOW_UNMODIFIED
When generating output, include the names of unmodified files if they are included in the git_diff. Normally these are skipped in the formats that list files (e.g. name-only, name-status, raw). Even with this, these will not be included in patch format.

GIT_DIFF_PATIENCE
Use the \"patience diff\" algorithm

GIT_DIFF_MINIMAL
Take extra time to find minimal diff

GIT_DIFF_SHOW_BINARY
Include the necessary deflate / delta information so that git-apply can apply given diff information to binary files.
"
    .trim();
    let mut flag: Option<Flag> = None;
    let mut flags: Vec<Flag> = vec![];
    content.lines().for_each(|line| {
        if line.starts_with("GIT_DIFF_") {
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

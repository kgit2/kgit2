use cli_clipboard::{ClipboardContext, ClipboardProvider};
use convert_case::{Case, Casing};
use std::env;
use std::fmt::{Display, Formatter};

struct Payload<'a> {
    cb_name: &'a str,
    pub payload_name: String,
}

impl<'a> Payload<'a> {
    fn new(cb_name: &'a str) -> Payload {
        Payload {
            cb_name,
            payload_name: format!("{}Payload", cb_name),
        }
    }
}

impl<'a> Display for Payload<'a> {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
interface {} {{
    var {}: {}?
}}
        ",
            self.payload_name,
            self.cb_name.to_case(Case::Camel),
            self.cb_name
        )
    }
}

struct StaticCallback<'a> {
    cb_name: String,
    lg_name: &'a str,
    param_count: i32,
    payload_name: &'a String,
}

impl<'a> StaticCallback<'a> {
    fn new(
        cb_name: &str,
        lg_name: &'a str,
        param_count: i32,
        payload_name: &'a String,
    ) -> StaticCallback<'a> {
        StaticCallback {
            cb_name: format!("static{}", cb_name),
            lg_name,
            param_count,
            payload_name,
        }
    }
}

impl<'a> Display for StaticCallback<'a> {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
val {}: {} = staticCFunction {{
    {}payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<{}>()?.get()
    0
}}",
            self.cb_name,
            self.lg_name,
            "_, ".repeat(self.param_count as usize - 1),
            self.payload_name,
        )
    }
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() != 3 {
        println!("Usage : callback [lg_name] [param_count]");
        panic!("args error");
    }
    let lg_name = &args[1];
    let param_count = args[2].parse::<i32>().unwrap();
    let k_name = lg_name
        .replace("git_", "")
        .replace("_cb", "_callback")
        .to_case(Case::UpperCamel);

    let payload = Payload::new(&k_name);
    let static_cb = StaticCallback::new(&k_name, lg_name, param_count, &payload.payload_name);
    let code = format!("{}\n{}\n", payload, static_cb);
    let mut ctx = ClipboardContext::new().unwrap();
    ctx.set_contents(code).unwrap();
}

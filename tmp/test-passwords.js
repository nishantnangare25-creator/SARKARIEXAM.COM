import * as ftp from "basic-ftp";

async function testFTP(pass) {
    const client = new ftp.Client();
    client.ftp.verbose = true;
    try {
        console.log(`Testing password: ${pass}`);
        await client.access({
            host: "sarkariexamai.com",
            user: "253@sarkariexamai.com",
            password: pass,
            secure: false
        });
        console.log("✅ Success!");
    } catch (err) {
        console.log(`❌ Failed: ${err.message}`);
    }
    client.close();
}

const pass1 = "30nW=+YeE&G!m2&4";
const pass2 = "NA45&#$re123";

async function run() {
    await testFTP(pass1);
    await testFTP(pass2);
}

run();

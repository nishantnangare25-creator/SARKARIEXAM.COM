import * as ftp from "basic-ftp";
import * as fs from "fs";

async function runTest() {
    const client = new ftp.Client();
    client.ftp.verbose = false;
    try {
        await client.access({
            host: "sarkariexamai.com",
            user: "253@sarkariexamai.com",
            password: "NA45&#$re123",
            secure: false
        });

        // Create local test files
        fs.writeFileSync("test-root.txt", "ROOT_FOLDER");
        fs.writeFileSync("test-public.txt", "PUBLIC_HTML_FOLDER");

        // Upload to FTP Root (/)
        await client.uploadFrom("test-root.txt", "gemini-test.html");

        // Upload to public_html/
        await client.ensureDir("public_html");
        await client.uploadFrom("test-public.txt", "gemini-test.html");
        
        console.log("Test files uploaded.");
    }
    catch (err) {
        console.error(err);
    }
    client.close();
}

runTest();

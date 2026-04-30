import * as ftp from "basic-ftp";

async function cleanAndDeploy() {
    const client = new ftp.Client();
    client.ftp.verbose = true;
    try {
        console.log("Connecting to MilesWeb FTP...");
        await client.access({
            host: "sarkariexamai.com",
            user: "253@sarkariexamai.com",
            password: "30nW=+YеЕ&G!m2&4",
            secure: false
        });

        console.log("Connected! Navigating to public_html...");
        await client.ensureDir("public_html");
        
        console.log("Deleting all old files and folders inside public_html...");
        await client.clearWorkingDir();
        
        console.log("Old files deleted. Uploading raw built files from 'dist'...");
        await client.uploadFromDir("dist");
        
        console.log("Upload Complete! The new files are now live in public_html.");
    }
    catch (err) {
        console.error("Clean Deployment failed:", err);
    }
    client.close();
}

cleanAndDeploy();

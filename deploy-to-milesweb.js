import * as ftp from "basic-ftp";

async function deploy() {
    const client = new ftp.Client();
    client.ftp.verbose = true; // Log progress
    try {
        console.log("Connecting to MilesWeb FTP...");
        await client.access({
            host: process.env.FTP_SERVER || "sarkariexamai.com",
            user: process.env.FTP_USERNAME,
            password: process.env.FTP_PASSWORD,
            secure: false
        });

        console.log("Connected! Navigating to public_html...");
        
        console.log("Uploading all new files from 'dist' directly into Root (server base directory)...");
        // uploadFromDir basically copies the contents of local 'dist' into remote current directory (Root)
        await client.uploadFromDir("dist");
        
        console.log("Upload Complete! The new files are now live.");
    }
    catch (err) {
        console.error("Deployment failed:", err);
    }
    client.close();
}

deploy();

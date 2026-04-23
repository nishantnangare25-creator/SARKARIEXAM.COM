import * as ftp from "basic-ftp";

async function deploy() {
    const client = new ftp.Client();
    client.ftp.verbose = true;

    try {
        console.log("Connecting to MilesWeb FTP...");
        client.ftp.ipFamily = 4;
        client.ftp.timeout = 120000;

        await client.access({
            host: process.env.FTP_SERVER || "103.86.176.249",
            user: process.env.FTP_USERNAME || "sarkariexam@sarkariexamai.com",
            password: process.env.FTP_PASSWORD || "15M~Ro>r5vRrL}3<",
            secure: false
        });

        console.log("Connected! Navigating to public_html...");
        await client.ensureDir("public_html");

        console.log("Uploading files from 'dist'...");
        await client.uploadFromDir("dist");

        console.log("Upload Complete! The new files are now live.");
    }
    catch (err) {
        console.error("Deployment failed:", err);
        process.exit(1);
    }
    finally {
        client.close();
    }
}

deploy();

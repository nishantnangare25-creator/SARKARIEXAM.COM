import * as ftp from "basic-ftp";
import * as fs from "fs";
import * as path from "path";

/**
 * Custom recursive upload that processes files one by one with a delay.
 * This is much more stable on servers with strict rate limits or BitNinja protection.
 */
async function uploadSlowly(client, localDir) {
    const entries = fs.readdirSync(localDir, { withFileTypes: true });
    
    for (const entry of entries) {
        const localPath = path.join(localDir, entry.name);
        
        if (entry.isDirectory()) {
            console.log(`📂 Entering Directory: ${entry.name}`);
            await client.ensureDir(entry.name);
            await uploadSlowly(client, localPath);
            await client.cd(".."); // Return to previous level
        } else {
            process.stdout.write(`📄 Uploading: ${entry.name.padEnd(40)}`);
            await client.uploadFrom(localPath, entry.name);
            console.log(" [DONE]");
            // 500ms gap helps prevent BitNinja from flagging the burst of connections
            await new Promise(r => setTimeout(r, 500));
        }
    }
}

async function deployWithRetry(maxRetries = 5) {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
        const client = new ftp.Client();
        // client.ftp.verbose = true; // High verbosity for debugging
        
        try {
            console.log(`\n🚀 Deployment attempt ${attempt}/${maxRetries}...`);
            client.ftp.ipFamily = 4;
            client.ftp.timeout = 180000; // 3 minutes timeout

            await client.access({
                host: process.env.FTP_SERVER || "103.86.176.249",
                user: process.env.FTP_USERNAME || "sarkariexam@sarkariexamai.com",
                password: process.env.FTP_PASSWORD || "15M~Ro>r5vRrL}3<",
                secure: false
            });

            const pwd = await client.pwd();
            console.log("Logged in. Current directory:", pwd);

            if (pwd === "/" || pwd === "") {
                console.log("Navigating into public_html...");
                await client.ensureDir("public_html");
            }
            
            console.log("Starting slow upload strategy (Slow & Steady)...");
            await uploadSlowly(client, "dist");
            
            console.log("\n✅ SUCCESS: Website is now live on MilesWeb.");
            client.close();
            return; 
        } 
        catch (err) {
            console.error(`❌ Attempt ${attempt} failed:`, err.message);
            client.close();
            
            if (attempt === maxRetries) {
                console.error("⛔ Max retries reached. Deployment failed.");
                throw err;
            }
            
            const delay = attempt * 10000; // 10s, 20s, 30s...
            console.log(`🔄 Waiting ${delay / 1000} seconds before next attempt...`);
            await new Promise(resolve => setTimeout(resolve, delay));
        }
    }
}

console.log("Starting Optimized Deployment for MilesWeb (BitNinja Compatible)...");
deployWithRetry().catch(err => {
    console.error("FATAL ERROR: Deployment failed. Please check FTP credentials and server status.");
    process.exit(1);
});

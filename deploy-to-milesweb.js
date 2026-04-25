import * as ftp from "basic-ftp";
import * as fs from "fs";
import * as path from "path";

/**
 * Upload files one-by-one with delay + per-file retry.
 * Passive mode + keepalive fixes ECONNRESET on MilesWeb/BitNinja.
 */
async function uploadSlowly(client, localDir) {
    const entries = fs.readdirSync(localDir, { withFileTypes: true });

    for (const entry of entries) {
        const localPath = path.join(localDir, entry.name);

        if (entry.isDirectory()) {
            console.log(`📂 Entering Directory: ${entry.name}`);
            await client.ensureDir(entry.name);
            await uploadSlowly(client, localPath);
            await client.cd("..");
        } else {
            // Per-file retry (up to 3 times)
            let uploaded = false;
            for (let fileAttempt = 1; fileAttempt <= 3; fileAttempt++) {
                try {
                    process.stdout.write(`📄 Uploading: ${entry.name.padEnd(42)}`);
                    await client.uploadFrom(localPath, entry.name);
                    console.log(" [DONE]");
                    uploaded = true;
                    break;
                } catch (fileErr) {
                    console.log(` [RETRY ${fileAttempt}/3] ${fileErr.message}`);
                    await new Promise(r => setTimeout(r, 3000 * fileAttempt));
                }
            }
            if (!uploaded) {
                throw new Error(`Failed to upload ${entry.name} after 3 attempts`);
            }
            // 800ms gap prevents BitNinja rate-limiting
            await new Promise(r => setTimeout(r, 800));
        }
    }
}

async function deployWithRetry(maxRetries = 5) {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
        const client = new ftp.Client();

        try {
            console.log(`\n🚀 Deployment attempt ${attempt}/${maxRetries}...`);

            // Force IPv4 + increase timeout
            client.ftp.ipFamily = 4;
            client.ftp.timeout = 300000; // 5 minutes

            await client.access({
                host: process.env.FTP_SERVER || "103.86.176.249",
                user: process.env.FTP_USERNAME || "sarkariexam@sarkariexamai.com",
                password: process.env.FTP_PASSWORD || "15M~Ro>r5vRrL}3<",
                secure: false,
                // Passive mode = server opens port, avoids firewall/BitNinja blocks
                passive: true,
                pasvTimeout: 60000,
                connTimeout: 60000,
                keepalive: 30000  // Send keepalive every 30s to prevent ECONNRESET
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
        } catch (err) {
            console.error(`❌ Attempt ${attempt} failed:`, err.message);
            client.close();

            if (attempt === maxRetries) {
                console.error("⛔ Max retries reached. Deployment failed.");
                throw err;
            }

            const delay = attempt * 15000; // 15s, 30s, 45s, 60s
            console.log(`🔄 Waiting ${delay / 1000}s before next attempt...`);
            await new Promise(resolve => setTimeout(resolve, delay));
        }
    }
}

console.log("Starting Optimized Deployment for MilesWeb (BitNinja Compatible)...");
deployWithRetry().catch(err => {
    console.error("FATAL ERROR: Deployment failed. Please check FTP credentials and server status.");
    process.exit(1);
});

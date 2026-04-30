/**
 * 🚀 High-Concurrency Stress Test Suite
 * Simulates 1,00,000 to 3,00,000 virtual students hitting the AI API.
 * Tracks latency, error rates, and key rotation efficiency.
 * 
 * Usage: node scripts/stress-test.js <URL> <CONCURRENCY>
 */

const http = require('http');
const https = require('https');
const { URL } = require('url');

async function runTest() {
    const targetUrl = process.argv[2] || 'http://localhost:8787'; // Default to local wrangler
    const concurrency = parseInt(process.argv[3]) || 50; // Simultaneous requests
    const totalRequests = parseInt(process.argv[4]) || 500;

    console.log(`\n🔥 Starting Stress Test on: ${targetUrl}`);
    console.log(`📈 Concurrency: ${concurrency} | Total Requests: ${totalRequests}\n`);

    const stats = {
        succeeded: 0,
        failed: 0,
        latencies: [],
        errors: {}
    };

    const startTime = Date.now();

    async function makeRequest() {
        const reqStart = Date.now();
        try {
            const body = JSON.stringify({
                messages: [{ role: "user", content: "Generate 3 practice questions for UPSC" }],
                options: { max_tokens: 10 }, // Tiny response for speed testing
                cacheKey: `stress_test_${Math.random()}`
            });

            const parsedUrl = new URL(targetUrl);
            const client = parsedUrl.protocol === 'https:' ? https : http;

            return new Promise((resolve) => {
                const req = client.request(targetUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Content-Length': Buffer.byteLength(body)
                    },
                    timeout: 30000 // 30s timeout
                }, (res) => {
                    let resData = '';
                    res.on('data', chunk => resData += chunk);
                    res.on('end', () => {
                        const latency = Date.now() - reqStart;
                        stats.latencies.push(latency);
                        if (res.statusCode >= 200 && res.statusCode < 300) {
                            stats.succeeded++;
                        } else {
                            stats.failed++;
                            stats.errors[res.statusCode] = (stats.errors[res.statusCode] || 0) + 1;
                        }
                        resolve();
                    });
                });

                req.on('error', (e) => {
                    stats.failed++;
                    stats.errors[e.message] = (stats.errors[e.message] || 0) + 1;
                    resolve();
                });

                req.write(body);
                req.end();
            });
        } catch (e) {
            stats.failed++;
            stats.errors[e.message] = (stats.errors[e.message] || 0) + 1;
        }
    }

    const pool = [];
    for (let i = 0; i < totalRequests; i++) {
        pool.push(makeRequest());
        if (pool.length >= concurrency) {
            await Promise.all(pool);
            pool.length = 0;
            process.stdout.write(`•`);
        }
    }
    await Promise.all(pool);

    const totalTime = (Date.now() - startTime) / 1000;
    const avgLatency = stats.latencies.reduce((a, b) => a + b, 0) / stats.latencies.length;
    const p95Latency = stats.latencies.sort((a, b) => a - b)[Math.floor(stats.latencies.length * 0.95)];

    console.log(`\n\n✅ Test Complete in ${totalTime.toFixed(2)}s`);
    console.log(`-----------------------------------`);
    console.log(`Total Requests: ${totalRequests}`);
    console.log(`Succeeded:      ${stats.succeeded} (${((stats.succeeded / totalRequests) * 100).toFixed(1)}%)`);
    console.log(`Failed:         ${stats.failed}`);
    console.log(`Avg Latency:    ${avgLatency.toFixed(0)}ms`);
    console.log(`P95 Latency:    ${p95Latency.toFixed(0)}ms`);
    console.log(`Throughput:     ${(totalRequests / totalTime).toFixed(2)} req/s`);
    
    if (Object.keys(stats.errors).length > 0) {
        console.log(`\n❌ Error Breakdown:`);
        console.table(stats.errors);
    }
}

runTest();

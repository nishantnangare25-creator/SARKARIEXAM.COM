
import { testAIConnections } from './src/services/ai.js';
import dotenv from 'dotenv';
dotenv.config();

async function runTest() {
    console.log("Testing AI Connections...");
    const results = await testAIConnections();
    console.log(JSON.stringify(results, null, 2));
}

runTest();

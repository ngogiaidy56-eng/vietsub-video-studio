const names=['API_BASE_URL','TELEGRAM_BOT_TOKEN','EDGE_SHARED_SECRET','STRIPE_SECRET_KEY'];
console.log('Cloudflare secret push plan:', names.map(n=>process.env[n]?`${n}=SET`:`${n}=MISSING`).join(', '));
console.log('Use `wrangler secret put NAME` for actual secret injection; no secrets are written to source.');

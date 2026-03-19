# EDUAI Frontend (Vite + Vue3)

## Environment variables

Vite only exposes env vars prefixed with `VITE_`.

- **`VITE_API_BASE_URL`**: backend API base URL (the code defaults to `/api`)
  - Example: `http://localhost:8080`
- **`VITE_SIM_BASE_URL`**: SIM service base URL for “模板实验 / 立即实验” iframe and “进入 SIM 中心”
  - Default in code: `http://localhost:3000`
  - Example: `http://127.0.0.1:3000`

Create a local file (not committed) like `frontend/.env.local`:

```bash
VITE_API_BASE_URL=http://localhost:8080
VITE_SIM_BASE_URL=http://localhost:3000
```















































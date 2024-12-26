- при локальному запуску спочатку *npm i*
- запустити проєкт *npm run start*



## API Endpoints `https://financeappbackend-t7l2.onrender.com/`  (`http://localhost:8080` )

| Method | URL | Query | Body | Response | bearer token |
|--------|-----|--------|------|----------|--------------|
| **POST** | /api/users/register/ | - | {name, email, password, currency, referalCode*} | {token} | no |
| **POST** | /api/users/login/ | - |  {email, password} | {token} | no |
| **POST** | /api/users/logout/ | - |  - | {message} | yes |
| **POST** | /api/users/password_recovery/ | - |  {name, email, currency} | {token} | no |
| **GET** | /api/users/data/ | - |  - | {user : {name, email, password, currency, referalCode, role}} | yes |
| **PUT** | /api/users/data/ |  |  {name, password} | {user : {name, email, password, currency, referalCode, role}} | yes |
| **DELETE** | /api/users/ | - |  - | - | yes |
|--------|-----|-------|------|----------|------|
| **GET** | /api/currency/ | - | - | {success, symbols: { "AED": "United Arab Emirates Dirham", ...}} | no |
| **GET** | /api/currency/rates/ | - | - | success, timestamp, base, date, rates: { "AED": 3.829626, ...} | no |
| **GET** | /api/currency/rates/main/ | - | - | {success, timestamp, base, date, rates: { USD: 1.042646, EUR: 1, UAH: 43.648422}} | no |
|--------|-----|-------|------|----------|------|
| **GET** | /api/group/users/ | - | - | {referalCode, currency, users: [ {_id, name, email, role}]} | yes |
| **DELETE** | /api/group/users/ | userId | - | - | yes |
|--------|-----|-------|------|----------|------|
| **POST** | /api/finance/categories/ | - | {title} | {message} | yes |
| **GET** | /api/finance/categories/all/ | - | - | {categories: [{_id, title, balanceId}]} | yes |
| **DELETE** | /api/finance/categories/ | id | - | - | yes |
| **POST** | /api/finance/records/ | - | {type, title, value, method, date, categoryId, reccurent, repeating*} | {message} | yes |
| **GET** | /api/finance/records/all/ | - | - | {records: [ {id, balanceId, type, title, value, method, category, reccurent, repeating} ]} | yes |
| **DELETE** | /api/finance/records/ | id | - | - | yes |
| **GET** | /api/finance/balance/current/ | - | - | {currency, currentMonth, incomeTotal, expenseTotal, total} | yes |
| **GET** | /api/finance/balance/current/categories/ | - | - | {currency, currentMonth, categories: [title, total, categoryId]} | yes |
| **GET** | /api/finance/balance/current/category/ | categoryId | - | {category: {title, total, currency, records: [{_id, type, title, value, date}]}} | yes |
|--------|-----|-------|------|----------|------|
| **GET** | /api/statistics/ | - |  - | {totalSpent, totalIncome, totalEarnings, totalSavings, totalExpenditure, totalCategories,} | yes |
|--------|-----|-------|------|----------|------|

- req = "date": "12.12.2024" (MM.DD.YYYY) | res = "date": "2024-12-11T23:00:00.000Z" (YYYYDD-MMTHH:MM:SS.000Z)

### Examples:
#### 1. Get Currency
- **URL**: `/api/currency`
- **Method**: `GET`
- **Response**:
  - `200 OK`: JSON object containing all available currencies.

#### 2. Get Currency Rates
- **URL**: `/api/currency/rates`
- **Method**: `GET`
- **Response**:
  - `200 OK`: JSON object with all currency rates.

#### 3. Get Main Currency Rates
- **URL**: `/api/currency/rates/main`
- **Method**: `GET`
- **Response**:
  - `200 OK`: JSON object with the main currency rates.
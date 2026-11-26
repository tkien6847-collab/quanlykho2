# Quản lý kho

Ứng dụng web quản lý kho gồm giao diện React/TypeScript và REST API Spring Boot. Hệ thống hỗ trợ đăng nhập theo vai trò, quản lý hàng hóa và danh mục, lập và duyệt phiếu nhập/xuất, quản lý đối tác/nhân viên/tài khoản và xem báo cáo tồn kho.

## Công nghệ

- **Frontend:** React 18, TypeScript, Vite, Tailwind CSS và CSS tùy chỉnh. Dự án không dùng Bootstrap.
- **Backend:** Java 21, Spring Boot 3, Spring MVC, Spring Security, Spring Data JPA, JWT.
- **Cơ sở dữ liệu:** MySQL.
- **API client:** Postman collection trong `postman/`.

## Cấu trúc thư mục

```text
.
├── backend/
│   ├── src/main/java/com/qlykho/
│   │   ├── controller/   # REST endpoints và xử lý ngoại lệ
│   │   ├── dto/          # Request/response API
│   │   ├── entity/       # Mô hình JPA
│   │   ├── repository/   # Truy vấn dữ liệu
│   │   ├── security/     # JWT filter và cấu hình quyền
│   │   └── service/      # Nghiệp vụ và dữ liệu khởi tạo
│   ├── src/main/resources/application.properties
│   ├── .env.example
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/   # Trang đăng nhập và workspace nghiệp vụ
│   │   ├── layout/       # Header, sidebar, footer
│   │   ├── services/     # Gọi API
│   │   ├── App.tsx       # Khung ứng dụng và trạng thái đăng nhập
│   │   ├── types.ts      # Kiểu dữ liệu giao diện/API
│   │   └── styles.css
│   ├── .env.example
│   └── package.json
└── postman/              # Collection và biến Postman
```

## Yêu cầu

- JDK 21.
- Node.js phiên bản LTS và npm.
- MySQL đang chạy, tài khoản có quyền tạo/truy cập database `qlykho`.
- Maven (hoặc Maven Wrapper nếu được bổ sung trong môi trường triển khai).

## Cấu hình backend

Trong terminal PowerShell:

```powershell
Copy-Item backend/.env.example backend/.env
```

Spring Boot không tự đọc file `.env`; hãy đặt các biến trong môi trường chạy hoặc cấu hình Environment Variables trong IDE. Các biến chính:

| Biến | Mặc định | Ý nghĩa |
|---|---|---|
| `JWT_SECRET` | Không có | Khóa ký JWT, tối thiểu 32 byte/ký tự ASCII an toàn. |
| `DB_URL` | `jdbc:mysql://localhost:3306/qlykho?...` | JDBC URL MySQL. |
| `DB_USERNAME` | `root` | Tên đăng nhập MySQL. |
| `DB_PASSWORD` | `123456` | Mật khẩu MySQL; cần đổi theo máy chạy. |
| `FRONTEND_URL` | `http://localhost:5173` | Đích chuyển hướng từ backend `/`. |
| `FRONTEND_ORIGIN` | `http://localhost:5173` | Origin được phép gọi API qua CORS. |
| `APP_BOOTSTRAP_ADMIN_*` | Xem `backend/.env.example` | Thông tin quản trị khởi tạo nếu chưa có tài khoản. |

`backend/src/main/resources/application.properties` cấu hình Hibernate `ddl-auto=update`, vì vậy schema được đồng bộ khi ứng dụng khởi động. Kiểm tra và sao lưu database trước khi dùng dữ liệu thật.

Chạy backend:

```powershell
$env:JWT_SECRET = "thay-bang-khoa-bi-mat-it-nhat-32-ky-tu"
$env:DB_URL = "jdbc:mysql://localhost:3306/qlykho?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mat-khau-mysql"
cd backend
mvn spring-boot:run
```

API mặc định chạy tại `http://localhost:8080`. Nếu database không ở cấu hình mặc định, hãy sửa biến tương ứng. Ứng dụng tạo tài khoản bootstrap theo `APP_BOOTSTRAP_ADMIN_*` khi khởi động nếu tài khoản đó chưa tồn tại; hãy đổi thông tin mặc định trước khi triển khai.

## Cấu hình và chạy frontend

```powershell
Copy-Item frontend/.env.example frontend/.env
cd frontend
npm install
npm run dev
```

Trong phát triển, Vite phục vụ ứng dụng tại `http://localhost:5173`. Frontend gọi API cùng origin theo mặc định; đặt `VITE_API_BASE_URL` trong `frontend/.env` chỉ khi API nằm ở origin khác.

### Chia sẻ bản phát triển qua ngrok

Khởi động backend trên cổng `8081` và Vite trên cổng `5173`. Vite tự chuyển tiếp các request `/api` sang backend, vì vậy ngrok cần trỏ tới Vite:

```powershell
ngrok http 5173 --url=https://numeral-tricolor-batch.ngrok-free.dev
```

Mở `https://numeral-tricolor-batch.ngrok-free.dev` để dùng ứng dụng. Vite cần được khởi động lại sau khi sửa `vite.config.ts`. Backend cho phép CORS từ domain ngrok này theo cấu hình mặc định.

Các lệnh frontend:

- `npm run dev`: chạy môi trường phát triển.
- `npm run build`: kiểm tra TypeScript và tạo bản production trong `frontend/dist/`.
- `npm run preview`: xem thử bản build production.

## Chức năng và quyền

Giao diện lấy vai trò từ danh tính trả về bởi backend; token JWT được giữ trong `sessionStorage` và gửi trong header Bearer khi gọi API. Backend kiểm tra xác thực và quyền truy cập, do đó việc ẩn chức năng trên giao diện không thay thế kiểm tra quyền ở API.

- **Nhân viên:** tra cứu và quản lý hàng hóa theo quyền API, lập phiếu nhập/xuất, xem tồn kho.
- **Quản lý:** quản lý nhân viên, tạo tài khoản nhân viên, quản lý đối tác, hàng hóa, duyệt phiếu và xem báo cáo.
- **Quản trị viên:** quản lý tài khoản, xem báo cáo và duyệt phiếu nhập theo phân quyền hiện tại.

Phiếu nhập/xuất mới có trạng thái chờ duyệt. Số liệu tồn kho và báo cáo được tính từ dữ liệu chi tiết phiếu đã lưu theo nghiệp vụ ở backend.

## API và Postman

Các endpoint được triển khai trong `backend/src/main/java/com/qlykho/controller/`; đăng nhập dùng `POST /api/auth/login`, thông tin phiên hiện tại dùng `GET /api/auth/me`. Các API nghiệp vụ nằm dưới `/api`. Có thể nhập collection và globals trong `postman/` vào Postman; collection hiện có thể còn là bộ khung, hãy bổ sung request cụ thể theo nhu cầu kiểm thử.

## Ghi chú repository

`frontend/node_modules/`, `frontend/dist/` và `backend/target/` là thư mục sinh ra khi cài/build, đã được thêm vào `.gitignore`. Không cần commit các thư mục này. Source frontend hiện được chia thành `components/`, `layout/`, `services/`; mã backend vẫn theo package Spring (`controller`, `dto`, `entity`, `repository`, `security`, `service`) để component scan và JPA hoạt động như hiện tại.

# Frontend quản lý kho

Ứng dụng được tạo bằng React, TypeScript, Vite và Tailwind CSS.

## Chạy ứng dụng

1. Cài Node.js LTS.
2. Tại thư mục `frontend`, chạy `npm install`.
3. Sao chép `.env.example` thành `.env` nếu backend không chạy tại `http://localhost:8080`, sau đó cập nhật `VITE_API_BASE_URL`.
4. Chạy `npm run dev` và mở địa chỉ Vite hiển thị trong terminal.

Chạy `npm run build` để kiểm tra TypeScript và tạo bản production trong `dist`.

Khi cả frontend và backend đang chạy theo cấu hình mặc định, truy cập `http://localhost:8080` sẽ tự chuyển đến giao diện frontend tại `http://localhost:5173` (trang đăng nhập nếu chưa đăng nhập). Có thể đổi đích chuyển hướng bằng biến môi trường `FRONTEND_URL`.

## Tích hợp backend

Trang đăng nhập gọi `POST /api/auth/login`. Token chỉ được giữ trong session của trình duyệt; tải lại trang sẽ xác thực lại với `GET /api/auth/me`. Vai trò lấy từ tài khoản do backend trả về, không có bộ chọn vai trò trên giao diện. Nút đăng xuất xóa phiên đăng nhập hiện tại.

Các màn hình quản lý dữ liệu gọi API hiện có dưới `/api` và gửi JWT Bearer. Backend cho phép CORS từ `http://localhost:5173` mặc định. Màn hình thống kê hiển thị tổng số lượng hàng nhập và xuất trong ngày hiện tại và từ đầu tháng đến ngày hiện tại, cộng từ chi tiết tất cả phiếu đã lưu.

Để khởi chạy backend, cấu hình `JWT_SECRET` (ít nhất 32 byte) và các biến cơ sở dữ liệu. Khi khởi động, backend tự tạo tài khoản quản trị `adminso1` với mật khẩu `Admin@123` nếu tài khoản này chưa tồn tại. Đây là thông tin mặc định dùng cho môi trường phát triển; hãy thay đổi các biến `APP_BOOTSTRAP_ADMIN_*` trước khi triển khai thực tế. Mật khẩu bootstrap cần ít nhất 8 ký tự. Hồ sơ nhân viên quản trị được tạo cùng tài khoản.

Các endpoint tài khoản và danh sách tài khoản chỉ dành cho quản trị viên; biểu mẫu tạo tài khoản quản lý chỉ cần tên đăng nhập và mật khẩu, hồ sơ nhân viên được backend tạo tự động. Quản lý có mục riêng để tạo tài khoản nhân viên chỉ với tên đăng nhập và mật khẩu; vai trò Nhân viên được gán tự động và tài khoản mới sẽ có trong danh sách của quản trị viên. Quản lý được xem, thêm, sửa, xóa hồ sơ nhân viên; không thể xóa hồ sơ đã gắn tài khoản hoặc có phiếu nhập/xuất. Phiếu nhập/xuất mới luôn được tạo ở trạng thái “Chờ duyệt”; quản lý có thể đồng ý duyệt phiếu để chuyển sang “Đã duyệt”. Nhà cung cấp/khách hàng, báo cáo và duyệt phiếu dành cho quản lý; báo cáo cũng dành cho quản trị viên; lập phiếu dành cho nhân viên. Màn hình chỉ hiển thị chức năng theo vai trò được xác thực và backend cũng kiểm tra JWT/role.

Khi thêm hoặc sửa hàng hóa, nhập tên danh mục; danh mục cùng tên (không phân biệt hoa thường, bỏ khoảng trắng đầu/cuối) dùng chung một mã và danh mục chưa có sẽ được tạo tự động. Một số thao tác nghiệp vụ khác vẫn yêu cầu mã nhân viên, mã nhà cung cấp/khách hàng và mã hàng hóa đã có trong cơ sở dữ liệu.

Nhân viên và quản lý có thể tra cứu tồn kho theo tên danh mục và trạng thái: “Hết hàng” là 0, “Sắp hết” là 1–5, “Còn hàng” là trên 5 sản phẩm.

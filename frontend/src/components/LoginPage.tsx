import { useState, type FormEvent } from "react";
import { apiRequest } from "../services/api";
import type { LoginResponse } from "../types";

interface LoginPageProps {
  onLogin: (session: LoginResponse) => void;
  initialMessage?: string;
}

export default function LoginPage({ onLogin, initialMessage = "" }: LoginPageProps) {
  const [tenDangNhap, setTenDangNhap] = useState("");
  const [matKhau, setMatKhau] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState(initialMessage);
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const session = await apiRequest<LoginResponse>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ tenDangNhap, matKhau }),
      });
      onLogin(session);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể đăng nhập.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-form-side">
        <div className="login-form-wrap">
          <div className="login-card-brand">
            <span className="brand-mark">K</span><strong>KhoFlow</strong>
          </div>
          <div className="login-heading">
            <span className="login-heading-icon">↗</span>
            <h2>Chào mừng trở lại</h2>
            <p>Đăng nhập bằng tài khoản được cấp để tiếp tục.</p>
          </div>
          <form className="login-form" onSubmit={(event) => void submit(event)}>
            {error && <div className="alert alert-error" role="alert">{error}</div>}
            <label className="field">
              <span>Tên đăng nhập</span>
              <div className="login-input-wrap">
                <span aria-hidden="true">♙</span>
                <input
                  autoComplete="username"
                  autoFocus
                  required
                  value={tenDangNhap}
                  onChange={(event) => setTenDangNhap(event.target.value)}
                  placeholder="Nhập tên đăng nhập"
                />
              </div>
            </label>
            <label className="field">
              <span>Mật khẩu</span>
              <div className="login-input-wrap">
                <span aria-hidden="true">♧</span>
                <input
                  autoComplete="current-password"
                  required
                  type={showPassword ? "text" : "password"}
                  value={matKhau}
                  onChange={(event) => setMatKhau(event.target.value)}
                  placeholder="Nhập mật khẩu"
                />
                <button className="password-toggle" type="button" onClick={() => setShowPassword((visible) => !visible)}>
                  {showPassword ? "Ẩn" : "Hiện"}
                </button>
              </div>
            </label>
            <button className="button button-primary login-submit" disabled={submitting}>
              {submitting ? "Đang xác thực..." : "Đăng nhập"} <span>→</span>
            </button>
          </form>
          <p className="login-help">Không có tài khoản? Vui lòng liên hệ quản trị viên hệ thống.</p>
        </div>
      </section>
    </main>
  );
}

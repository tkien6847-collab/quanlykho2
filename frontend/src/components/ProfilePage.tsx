import { useEffect, useState, type FormEvent } from "react";
import { apiRequest } from "../services/api";
import type { PersonalProfile } from "../types";

const profileEndpoint = "/api/auth/me/profile";

const fields = [
  { key: "hoTen", label: "Họ tên", type: "text", maxLength: 50 },
  { key: "gioiTinh", label: "Giới tính", type: "text", maxLength: 10 },
  { key: "ngaySinh", label: "Ngày sinh", type: "date", maxLength: undefined },
  { key: "soDienThoai", label: "Số điện thoại", type: "tel", maxLength: 20 },
  { key: "email", label: "Email", type: "email", maxLength: 100 },
  { key: "diaChi", label: "Địa chỉ", type: "text", maxLength: 200 },
] as const;

export default function ProfilePage() {
  const [profile, setProfile] = useState<PersonalProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  useEffect(() => {
    const controller = new AbortController();
    void apiRequest<PersonalProfile>(profileEndpoint, { signal: controller.signal })
      .then((data) => {
        if (!controller.signal.aborted) setProfile(data);
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(cause instanceof Error ? cause.message : "Không tải được thông tin cá nhân.");
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, []);

  const save = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!profile || saving) return;
    setSaving(true);
    setError("");
    setNotice("");
    try {
      const updatedProfile = await apiRequest<PersonalProfile>(profileEndpoint, {
        method: "PUT",
        body: JSON.stringify({ ...profile, ngaySinh: profile.ngaySinh || null }),
      });
      setProfile(updatedProfile);
      setNotice("Đã lưu thông tin cá nhân.");
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không lưu được thông tin cá nhân.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="workspace-page">
      <div className="page-heading"><h1>Thông tin cá nhân</h1></div>
      <section className="panel form-panel profile-panel">
        {loading && <div className="loading-state" role="status">Đang tải thông tin cá nhân…</div>}
        {error && <div className="alert alert-error" role="alert">{error}</div>}
        {notice && <div className="alert alert-success" role="status">{notice}</div>}
        {profile && (
          <form className="form-grid" autoComplete="off" onSubmit={(event) => void save(event)}>
            {fields.map(({ key, label, type, maxLength }) => (
              <label className="field" key={key}>
                <span>{label}</span>
                <input
                  type={type}
                  maxLength={maxLength}
                  disabled={saving}
                  value={profile[key] ?? ""}
                  onChange={(event) => {
                    const value = event.target.value;
                    setProfile((current) => current && { ...current, [key]: value });
                    setNotice("");
                  }}
                />
              </label>
            ))}
            <div className="form-span">
              <button type="submit" className="button button-primary" disabled={saving}>
                {saving ? "Đang lưu…" : "Lưu thay đổi"}
              </button>
            </div>
          </form>
        )}
      </section>
    </section>
  );
}

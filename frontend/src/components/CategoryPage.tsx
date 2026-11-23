import { useEffect, useState, type FormEvent } from "react";
import { apiRequest } from "../services/api";
import type { ActivityStatus, Category } from "../types";
import { normalizeSearch } from "../utils/search";

const endpoint = "/api/danh-muc";
const perPage = 5;

export default function CategoryPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [query, setQuery] = useState("");
  const [filter, setFilter] = useState("");
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");
  const [listError, setListError] = useState("");
  const [statusError, setStatusError] = useState("");
  const [updatingCategory, setUpdatingCategory] = useState<number | null>(null);
  const [notice, setNotice] = useState("");

  useEffect(() => {
    const controller = new AbortController();
    void apiRequest<Category[]>(endpoint, { signal: controller.signal })
      .then((data) => {
        if (!controller.signal.aborted) setCategories(data);
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setListError(cause instanceof Error ? cause.message : "Không tải được danh sách danh mục.");
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, []);

  const matching = categories.filter((category) => normalizeSearch(category.tenDM).includes(filter));
  const pageCount = Math.max(1, Math.ceil(matching.length / perPage));
  const currentPage = Math.min(page, pageCount);
  const start = (currentPage - 1) * perPage;
  const visibleCategories = matching.slice(start, start + perPage);

  const addCategory = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (loading || saving) return;
    setFormError("");
    setNotice("");
    setSaving(true);
    try {
      const created = await apiRequest<Category>(endpoint, {
        method: "POST",
        body: JSON.stringify({ tenDM: name, moTa: description }),
      });
      setCategories((current) => [created, ...current]);
      setName("");
      setDescription("");
      setQuery("");
      setFilter("");
      setPage(1);
      setNotice("Đã thêm danh mục.");
    } catch (cause) {
      setFormError(cause instanceof Error ? cause.message : "Không thêm được danh mục.");
    } finally {
      setSaving(false);
    }
  };

  const changeStatus = async (category: Category) => {
    if (updatingCategory !== null) return;
    const trangThai: ActivityStatus = category.trangThai === "Ngừng hoạt động" ? "Hoạt động" : "Ngừng hoạt động";
    setUpdatingCategory(category.maDM);
    setStatusError("");
    setNotice("");
    try {
      const updated = await apiRequest<Pick<Category, "maDM" | "trangThai">>(`${endpoint}/${category.maDM}/trang-thai`, {
        method: "PATCH",
        body: JSON.stringify({ trangThai }),
      });
      setCategories((current) => current.map((row) =>
        row.maDM === updated.maDM ? { ...row, trangThai: updated.trangThai } : row,
      ));
      setNotice("Đã cập nhật trạng thái danh mục.");
    } catch (cause) {
      setStatusError(cause instanceof Error ? cause.message : "Không cập nhật được trạng thái danh mục.");
    } finally {
      setUpdatingCategory(null);
    }
  };

  return (
    <section className="workspace-page category-workspace">
      <header className="category-toolbar">
        <h1>Quản lý danh mục</h1>
        <form role="search" className="category-search" onSubmit={(event) => {
          event.preventDefault();
          setFilter(normalizeSearch(query));
          setPage(1);
        }}>
          <input
            type="search"
            aria-label="Tìm theo tên danh mục"
            placeholder="Tìm theo tên…"
            value={query}
            onChange={(event) => {
              setQuery(event.target.value);
              if (!event.target.value) {
                setFilter("");
                setPage(1);
              }
            }}
          />
          <button type="submit" className="button button-secondary">Tìm</button>
        </form>
      </header>

      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      <div className="category-grid">
        <section className="panel category-form-card" aria-labelledby="category-new-title">
          <h2 id="category-new-title">Thêm danh mục mới</h2>
          {formError && <div className="alert alert-error" role="alert">{formError}</div>}
          <form className="form-stack" onSubmit={(event) => void addCategory(event)}>
            <label className="field">
              <span>Tên danh mục <b>*</b></span>
              <input
                required
                maxLength={100}
                value={name}
                disabled={saving}
                onChange={(event) => setName(event.target.value)}
              />
            </label>
            <label className="field">
              <span>Mô tả</span>
              <textarea
                rows={4}
                maxLength={200}
                value={description}
                disabled={saving}
                onChange={(event) => setDescription(event.target.value)}
              />
            </label>
            <button type="submit" className="button category-save" disabled={loading || saving || Boolean(listError)}>
              {saving ? "Đang lưu…" : "Lưu danh mục"}
            </button>
          </form>
        </section>

        <section className="panel category-list-card" aria-labelledby="category-list-title">
          <div className="category-list-header"><h2 id="category-list-title">Danh sách danh mục</h2></div>
          {listError && <div className="alert alert-error category-list-error" role="alert">{listError}</div>}
          {statusError && <div className="alert alert-error category-list-error" role="alert">{statusError}</div>}
          {loading ? <div className="loading-state" role="status">Đang tải danh mục…</div> : !listError && (
            <>
              <div className="table-wrap">
                <table className="category-table">
                  <thead>
                    <tr>
                      <th scope="col">STT</th>
                      <th scope="col">Tên danh mục</th>
                      <th scope="col">Mô tả</th>
                      <th scope="col">Số mặt hàng</th>
                      <th scope="col">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    {visibleCategories.map((category, index) => {
                      const active = category.trangThai !== "Ngừng hoạt động";
                      const action = active ? "Ngừng hoạt động" : "Kích hoạt";
                      return (
                        <tr key={category.maDM}>
                          <td>{start + index + 1}</td>
                          <th scope="row">{category.tenDM}</th>
                          <td>{category.moTa}</td>
                          <td>{category.soMatHang?.toLocaleString("vi-VN") ?? "—"}</td>
                          <td>
                            <div className="category-actions">
                              <span className={active ? "status-active" : "status-inactive"}>{active ? "Hoạt động" : "Ngừng hoạt động"}</span>
                              <button
                                type="button"
                                className="button button-secondary account-action-button"
                                disabled={updatingCategory !== null}
                                aria-label={`${action} danh mục ${category.tenDM}`}
                                onClick={() => void changeStatus(category)}
                              >{action}</button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                    {visibleCategories.length === 0 && (
                      <tr><td colSpan={5} className="category-empty">
                        {filter ? "Không tìm thấy danh mục phù hợp." : "Chưa có danh mục."}
                      </td></tr>
                    )}
                  </tbody>
                </table>
              </div>
              {matching.length > 0 && (
                <div className="category-list-footer">
                  <span>Trang {currentPage}/{pageCount} · {matching.length} danh mục</span>
                  <nav aria-label="Phân trang danh mục">
                    <button type="button" className="button button-secondary" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Trước</button>
                    <button type="button" className="button button-secondary" disabled={currentPage === pageCount} onClick={() => setPage(currentPage + 1)}>Sau</button>
                  </nav>
                </div>
              )}
            </>
          )}
        </section>
      </div>
    </section>
  );
}

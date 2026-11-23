import { useEffect, useRef, useState, type FormEvent } from "react";
import { apiRequest } from "../services/api";
import type { ActivityStatus } from "../types";
import { normalizeSearch } from "../utils/search";

type FormValues = Record<string, string>;

export interface DirectoryField {
  name: string;
  label: string;
  type: "text" | "email" | "tel" | "password" | "select";
  maxLength?: number;
  minLength?: number;
  required?: boolean;
  autoComplete?: string;
  options?: { value: number; label: string }[];
  emptyOption?: string;
}

export interface DirectoryConfig<T extends object> {
  noun: string;
  endpoint: string;
  getId: (item: T) => number;
  getName: (item: T) => string;
  columns: { key: keyof T; label: string }[];
  fields: DirectoryField[] | ((items: T[], values: FormValues) => DirectoryField[]);
  createEndpoint?: string;
  createTitle?: string;
  getStatus: (item: T) => ActivityStatus | null;
}

const pageSize = 7;

export default function DirectoryList<T extends object>({ config }: { config: DirectoryConfig<T> }) {
  const { noun, endpoint, getId, getName, columns, getStatus } = config;
  const [items, setItems] = useState<T[]>([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [values, setValues] = useState<FormValues>({});
  const [notice, setNotice] = useState("");
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
  const dialogRef = useRef<HTMLDialogElement>(null);
  const firstInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const controller = new AbortController();
    void apiRequest<T[]>(endpoint, { signal: controller.signal })
      .then((data) => {
        if (!controller.signal.aborted) setItems(data.sort((left, right) => getId(right) - getId(left)));
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(cause instanceof Error ? cause.message : `Không tải được danh sách ${noun}.`);
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, [endpoint, getId, noun]);

  const term = normalizeSearch(search);
  const filtered = items.filter((item) => normalizeSearch(getName(item)).includes(term));
  const pageCount = Math.max(1, Math.ceil(filtered.length / pageSize));
  const currentPage = Math.min(page, pageCount);
  const firstRow = (currentPage - 1) * pageSize;
  const rows = filtered.slice(firstRow, firstRow + pageSize);
  const formTitle = config.createTitle ?? `Thêm ${noun}`;
  const fields = typeof config.fields === "function" ? config.fields(items, values) : config.fields;
  const firstInputName = fields.find((field) => field.type !== "select")?.name;

  const showForm = () => {
    setValues({});
    setFormError("");
    dialogRef.current?.showModal();
    firstInputRef.current?.focus();
  };

  const updateField = (name: string, value: string) => {
    setValues((current) => ({ ...current, [name]: value }));
  };

  const save = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (saving) return;
    setFormError("");
    setSaving(true);
    try {
      const payload = Object.fromEntries(fields.map(({ name, type }) => {
        const value = values[name] ?? "";
        if (type === "password") return [name, value];
        if (type === "select") return [name, value ? Number(value) : null];
        return [name, value.trim() || null];
      }));
      const updated = await apiRequest<T>(config.createEndpoint ?? endpoint, {
        method: "POST",
        body: JSON.stringify(payload),
      });
      const updatedId = getId(updated);
      setItems((current) => [updated, ...current.filter((item) => getId(item) !== updatedId)]
        .sort((left, right) => getId(right) - getId(left)));
      setSearch("");
      setPage(Math.floor(items.filter((item) => getId(item) > updatedId).length / pageSize) + 1);
      setNotice(`Đã ${formTitle.toLocaleLowerCase("vi")}.`);
      setError("");
      setValues({});
      dialogRef.current?.close();
    } catch (cause) {
      setFormError(cause instanceof Error ? cause.message : `Không lưu được thông tin ${noun}.`);
    } finally {
      setSaving(false);
    }
  };

  const changeStatus = async (item: T) => {
    const status = getStatus(item);
    if (status === null || updatingStatus) return;
    const trangThai: ActivityStatus = status === "Hoạt động" ? "Ngừng hoạt động" : "Hoạt động";
    setUpdatingStatus(true);
    setError("");
    setNotice("");
    try {
      const updated = await apiRequest<T>(`${endpoint}/${getId(item)}/trang-thai`, {
        method: "PATCH",
        body: JSON.stringify({ trangThai }),
      });
      setItems((current) => current.map((row) => getId(row) === getId(updated) ? updated : row));
      setNotice(`Đã cập nhật trạng thái ${noun}.`);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không cập nhật được trạng thái.");
    } finally {
      setUpdatingStatus(false);
    }
  };

  return (
    <section className="panel data-panel account-list-panel" aria-labelledby="directory-title">
      <div className="account-toolbar">
        <h2 id="directory-title">Danh sách {noun}</h2>
        <div className="account-tools">
          <label className="account-search">
            <span aria-hidden="true">⌕</span>
            <input
              type="search"
              aria-label={`Tìm theo tên ${noun}`}
              placeholder="Tìm theo tên"
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setPage(1);
              }}
            />
          </label>
          <button type="button" className="button button-primary" disabled={loading} onClick={showForm}>
            + {formTitle}
          </button>
        </div>
      </div>

      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      {loading ? <div className="loading-state" role="status">Đang tải danh sách…</div> : (
        <>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th scope="col">STT</th>
                  {columns.map(({ key, label }) => <th key={String(key)} scope="col">{label}</th>)}
                  <th scope="col">Trạng thái</th>
                  <th scope="col">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((item, index) => {
                  const status = getStatus(item);
                  const active = status === "Hoạt động";
                  return (
                    <tr key={getId(item)}>
                      <td>{firstRow + index + 1}</td>
                      {columns.map(({ key }) => <td key={String(key)}>{String(item[key] ?? "")}</td>)}
                      <td>
                        {status
                          ? <span className={active ? "status-active" : "status-inactive"}>{status}</span>
                          : "Chưa có tài khoản"}
                      </td>
                      <td>
                        {status && (
                          <button
                            type="button"
                            className="button button-secondary account-action-button"
                            disabled={updatingStatus}
                            onClick={() => void changeStatus(item)}
                          >
                            {active ? "Ngừng hoạt động" : "Kích hoạt"}
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {rows.length === 0 && !error && (
              <div className="loading-state">{term ? `Không tìm thấy ${noun}.` : `Chưa có ${noun}.`}</div>
            )}
          </div>
          {filtered.length > 0 && (
            <div className="pagination">
              <span>Trang {currentPage}/{pageCount} · {filtered.length} {noun}</span>
              <button type="button" className="button button-secondary" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Trước</button>
              <button type="button" className="button button-secondary" disabled={currentPage === pageCount} onClick={() => setPage(currentPage + 1)}>Sau</button>
            </div>
          )}
        </>
      )}

      <dialog
        className="details-modal directory-dialog"
        ref={dialogRef}
        aria-labelledby="directory-form-title"
        onCancel={(event) => { if (saving) event.preventDefault(); }}
        onClose={() => setValues({})}
      >
        <div className="panel-heading">
          <h2 id="directory-form-title">{formTitle}</h2>
          <button type="button" className="text-button" disabled={saving} onClick={() => dialogRef.current?.close()}>Đóng</button>
        </div>
        {formError && <div className="alert alert-error" role="alert">{formError}</div>}
        <form className="form-stack" autoComplete="off" onSubmit={(event) => void save(event)}>
          {fields.map(({ name, label, type, maxLength, minLength, required, autoComplete, options, emptyOption }) => (
            <label className="field" key={name}>
              <span>{label}{required && <b> *</b>}</span>
              {type === "select" ? (
                <select name={name} disabled={saving} value={values[name] ?? ""} onChange={(event) => updateField(name, event.target.value)}>
                  <option value="">{emptyOption}</option>
                  {options?.map(({ value, label: optionLabel }) => <option key={value} value={value}>{optionLabel}</option>)}
                </select>
              ) : (
                <input
                  ref={firstInputName === name ? firstInputRef : undefined}
                  name={name}
                  type={type}
                  maxLength={maxLength}
                  minLength={minLength}
                  autoComplete={autoComplete}
                  required={required}
                  disabled={saving}
                  value={values[name] ?? ""}
                  onChange={(event) => updateField(name, event.target.value)}
                />
              )}
            </label>
          ))}
          <button type="submit" className="button button-primary" disabled={saving}>
            {saving ? "Đang lưu…" : formTitle}
          </button>
        </form>
      </dialog>
    </section>
  );
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8081";

export class ApiError extends Error {
  constructor(message: string, public readonly status: number) {
    super(message);
    this.name = "ApiError";
  }
}

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  const token = sessionStorage.getItem("accessToken");
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...init,
      headers: {
        ...(init?.body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...init?.headers,
      },
    });
  } catch {
    throw new Error(`Không thể kết nối máy chủ tại ${API_BASE_URL}.`);
  }

  if (!response.ok) {
    const problem = (await response.json().catch(() => null)) as { message?: string } | null;
    const message = typeof problem?.message === "string" ? problem.message : "";
    if (message) throw new ApiError(message, response.status);
    if (response.status === 401 && path === "/api/auth/login") {
      throw new ApiError("Tên đăng nhập hoặc mật khẩu không đúng.", response.status);
    }
    if (response.status === 401) {
      throw new ApiError("Phiên đăng nhập không còn hợp lệ. Vui lòng đăng nhập lại.", response.status);
    }
    if (response.status === 403) {
      throw new ApiError("Tài khoản không có quyền thực hiện thao tác này.", response.status);
    }
    throw new ApiError(`Yêu cầu thất bại (${response.status}).`, response.status);
  }

  if (response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

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
    if (response.status === 401) {
      if (path === "/api/auth/login") {
        throw new ApiError("Tên đăng nhập hoặc mật khẩu không đúng.", response.status);
      }
      throw new ApiError("Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.", response.status);
    }
    const body = await response.text();
    let message = body;
    try {
      const parsed = JSON.parse(body) as { message?: string };
      message = parsed.message ?? body;
    } catch {
      message = body;
    }
    throw new ApiError(message || `Yêu cầu thất bại (${response.status}).`, response.status);
  }

  if (response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

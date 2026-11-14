
import { useState } from "react";

import { Eye, EyeOff, LockKeyhole, Mail } from "lucide-react";
import api from "../../api/axiosConfig";
import { Link, useNavigate } from "react-router-dom";

export default function Login() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    try {
      setLoading(true);

      const response = await api.post("/auth/login", formData);

      const { token, ...user } = response.data;

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(user));

      navigate("/");
    } catch (error) {
      setError(
        error.response?.data?.message ||
        "Login failed. Please check your email and password."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">

        <div className="auth-brand">
          <div className="auth-logo">O</div>
          <h2>Opportunity360</h2>
          <p>Never forget what matters.</p>
        </div>

        <div className="auth-heading">
          <h1>Welcome Back!</h1>
          <p>Sign in to manage your events and reminders.</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">

          <div className="auth-field">
            <label htmlFor="email">Email Address</label>

            <div className="auth-input-wrapper">
              <Mail size={18} />
              <input
                id="email"
                type="email"
                name="email"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="auth-field">
            <label htmlFor="password">Password</label>

            <div className="auth-input-wrapper">
              <LockKeyhole size={18} />

              <input
                id="password"
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Enter your password"
                value={formData.password}
                onChange={handleChange}
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword((previous) => !previous)}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? (
                  <EyeOff size={18} />
                ) : (
                  <Eye size={18} />
                )}
              </button>
            </div>
          </div>

          {error && (
            <div className="auth-error">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="auth-submit-button"
            disabled={loading}
          >
            {loading ? "Signing in..." : "Sign In"}
          </button>

        </form>

        <div className="auth-register-link">
          Don't have an account?{" "}
          <Link to="/register">Create Account</Link>
        </div>

        <div className="auth-footer">
          <span>Secure access to your personal workspace</span>
        </div>

      </div>
    </div>
  );
}

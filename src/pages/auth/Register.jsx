
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
  User,
  ShieldCheck,
  ArrowLeft,
} from "lucide-react";
import api from "../../api/axiosConfig";

export default function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
  });

  const [otp, setOtp] = useState("");
  const [otpStep, setOtpStep] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (countdown <= 0) return;

    const timer = setTimeout(() => {
      setCountdown((previous) => previous - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [countdown]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // SEND OTP
  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (formData.password.length < 8) {
      setError("Password must contain at least 8 characters.");
      return;
    }

    try {
      setLoading(true);

      await api.post("/auth/register", {
        name: formData.name.trim(),
        email: formData.email.trim(),
        password: formData.password,
      });

      setOtpStep(true);
      setCountdown(60);
      setSuccess(
        `A verification OTP has been sent to ${formData.email.trim()}.`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to send OTP. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  // VERIFY OTP
  const handleVerifyOtp = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (!/^[0-9]{6}$/.test(otp)) {
      setError("Please enter a valid six-digit OTP.");
      return;
    }

    try {
      setLoading(true);

      await api.post("/auth/verify-email", {
        email: formData.email.trim(),
        otp: otp,
      });

      setSuccess(
        "Email verified successfully! Your account has been created."
      );

      setTimeout(() => {
        navigate("/login");
      }, 1800);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "OTP verification failed. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  // RESEND OTP
  const handleResendOtp = async () => {
    setError("");
    setSuccess("");

    try {
      setResending(true);

      await api.post("/auth/resend-otp", {
        email: formData.email.trim(),
      });

      setCountdown(60);
      setOtp("");
      setSuccess("A new OTP has been sent to your email.");
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to resend OTP. Please try again."
      );
    } finally {
      setResending(false);
    }
  };

  const handleChangeEmail = () => {
    setOtpStep(false);
    setOtp("");
    setCountdown(0);
    setError("");
    setSuccess("");
  };

  return (
    <div className="auth-page">
      <div className="auth-card">

        <div className="auth-brand">
          <div className="auth-logo">O</div>
          <h2>Opportunity360</h2>
          <p>Never forget what matters.</p>
        </div>

        {!otpStep ? (
          <>
            <div className="auth-heading">
              <h1>Create Account</h1>
              <p>Start managing your events and opportunities.</p>
            </div>

            <form onSubmit={handleSubmit} className="auth-form">

              <div className="auth-field">
                <label htmlFor="name">Full Name</label>

                <div className="auth-input-wrapper">
                  <User size={18} />
                  <input
                    id="name"
                    type="text"
                    name="name"
                    placeholder="Enter your full name"
                    value={formData.name}
                    onChange={handleChange}
                    maxLength={100}
                    required
                  />
                </div>
              </div>

              <div className="auth-field">
                <label htmlFor="register-email">Email Address</label>

                <div className="auth-input-wrapper">
                  <Mail size={18} />
                  <input
                    id="register-email"
                    type="email"
                    name="email"
                    placeholder="Enter your email"
                    value={formData.email}
                    onChange={handleChange}
                    maxLength={150}
                    required
                  />
                </div>
              </div>

              <div className="auth-field">
                <label htmlFor="register-password">Password</label>

                <div className="auth-input-wrapper">
                  <LockKeyhole size={18} />

                  <input
                    id="register-password"
                    type={showPassword ? "text" : "password"}
                    name="password"
                    placeholder="Create a password"
                    value={formData.password}
                    onChange={handleChange}
                    minLength={8}
                    required
                  />

                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() =>
                      setShowPassword((previous) => !previous)
                    }
                    aria-label={
                      showPassword ? "Hide password" : "Show password"
                    }
                  >
                    {showPassword ? (
                      <EyeOff size={18} />
                    ) : (
                      <Eye size={18} />
                    )}
                  </button>
                </div>
              </div>

              {error && <div className="auth-error">{error}</div>}

              {success && (
                <div className="auth-success">{success}</div>
              )}

              <button
                type="submit"
                className="auth-submit-button"
                disabled={loading}
              >
                {loading ? "Sending OTP..." : "Create Account"}
              </button>

            </form>
          </>
        ) : (
          <>
            <div className="auth-heading">
              <div style={{ marginBottom: "14px" }}>
                <ShieldCheck size={42} />
              </div>

              <h1>Verify Your Email</h1>

              <p>
                We've sent a six-digit verification code to
              </p>

              <strong>{formData.email}</strong>
            </div>

            <form onSubmit={handleVerifyOtp} className="auth-form">

              <div className="auth-field">
                <label htmlFor="verification-otp">
                  Enter Verification OTP
                </label>

                <div className="auth-input-wrapper">
                  <ShieldCheck size={18} />

                  <input
                    id="verification-otp"
                    type="text"
                    inputMode="numeric"
                    placeholder="Enter 6-digit OTP"
                    value={otp}
                    onChange={(event) =>
                      setOtp(
                        event.target.value
                          .replace(/\D/g, "")
                          .slice(0, 6)
                      )
                    }
                    maxLength={6}
                    required
                  />
                </div>
              </div>

              {error && <div className="auth-error">{error}</div>}

              {success && (
                <div className="auth-success">{success}</div>
              )}

              <button
                type="submit"
                className="auth-submit-button"
                disabled={loading || otp.length !== 6}
              >
                {loading ? "Verifying..." : "Verify Email"}
              </button>

              <div style={{ textAlign: "center", marginTop: "16px" }}>
                {countdown > 0 ? (
                  <p>
                    Resend OTP in <strong>{countdown}s</strong>
                  </p>
                ) : (
                  <button
                    type="button"
                    className="password-toggle"
                    onClick={handleResendOtp}
                    disabled={resending}
                  >
                    {resending ? "Sending..." : "Resend OTP"}
                  </button>
                )}
              </div>

              <button
                type="button"
                className="password-toggle"
                onClick={handleChangeEmail}
                style={{ marginTop: "12px" }}
              >
                <ArrowLeft size={16} />
                Change Email
              </button>

            </form>
          </>
        )}

        <div className="auth-register-link">
          Already have an account? <Link to="/login">Sign In</Link>
        </div>

      </div>
    </div>
  );
}

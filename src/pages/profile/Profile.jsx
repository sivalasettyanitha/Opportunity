
import { useState } from "react";
import {
  LockKeyhole,
  Save,
  Pencil,
} from "lucide-react";

import {
  updateProfile,
  changePassword,
} from "../../api/profileService";

export default function Profile() {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("user")) || {};
    } catch {
      return {};
    }
  });

  const [isEditing, setIsEditing] = useState(false);
  const [name, setName] = useState(user.name || "");
  const [email] = useState(user.email || "");

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const [profileLoading, setProfileLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);

  const [passwordForm, setPasswordForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const handleProfileUpdate = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    try {
      setProfileLoading(true);

      const response = await updateProfile(name);

      const updatedUser = {
        ...user,
        name: response.name || name,
      };

      localStorage.setItem("user", JSON.stringify(updatedUser));

      setUser(updatedUser);
      setName(updatedUser.name);
      setIsEditing(false);

      setMessage("Profile updated successfully!");
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to update profile. Please try again."
      );
    } finally {
      setProfileLoading(false);
    }
  };

  const handlePasswordChange = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    if (passwordForm.newPassword.length < 8) {
      setError("New password must contain at least 8 characters.");
      return;
    }

    if (
      passwordForm.newPassword !== passwordForm.confirmPassword
    ) {
      setError("New password and confirmation do not match.");
      return;
    }

    try {
      setPasswordLoading(true);

      await changePassword(
        passwordForm.currentPassword,
        passwordForm.newPassword
      );

      setPasswordForm({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });

      setMessage("Password changed successfully!");
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to change password. Please check your current password."
      );
    } finally {
      setPasswordLoading(false);
    }
  };

  return (
    <div className="profile-page">

      <div className="profile-header">
        <h1>My Profile</h1>
        <p>Manage your personal information and account security.</p>
      </div>

      <div className="profile-card">

        <div className="profile-card-header">
          <div className="profile-avatar">
            {(name || "U").charAt(0).toUpperCase()}
          </div>

          <div>
            <h2>{name || "Your Name"}</h2>
            <p>Personal Workspace</p>
          </div>
        </div>

        {message && (
          <div className="profile-message">{message}</div>
        )}

        {error && (
          <div className="auth-error">{error}</div>
        )}

        {/* Profile Information */}
        <form
          className="profile-form"
          onSubmit={handleProfileUpdate}
        >
          <div className="profile-form-grid">

            <div className="profile-form-field">
              <label htmlFor="profileName">Full Name</label>

              <input
                id="profileName"
                type="text"
                value={name}
                onChange={(event) => setName(event.target.value)}
                disabled={!isEditing || profileLoading}
                maxLength={100}
                required
              />
            </div>

            <div className="profile-form-field">
              <label htmlFor="profileEmail">Email Address</label>

              <input
                id="profileEmail"
                type="email"
                value={email}
                disabled
              />
            </div>

          </div>

          <div className="profile-form-actions">

            {!isEditing ? (
              <button
                type="button"
                className="primary-button"
                onClick={() => {
                  setMessage("");
                  setError("");
                  setIsEditing(true);
                }}
              >
                <Pencil size={15} />
                Edit Profile
              </button>
            ) : (
              <>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => {
                    setName(user.name || "");
                    setIsEditing(false);
                    setMessage("");
                    setError("");
                  }}
                  disabled={profileLoading}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-button"
                  disabled={profileLoading}
                >
                  <Save size={15} />
                  {profileLoading ? "Saving..." : "Save Changes"}
                </button>
              </>
            )}

          </div>
        </form>

        {/* Change Password */}
        <div className="profile-password-section">

          <h3>Change Password</h3>
          <p>Update your password to keep your account secure.</p>

          <form
            className="profile-form"
            onSubmit={handlePasswordChange}
          >

            <div className="profile-form-field">
              <label htmlFor="currentPassword">
                Current Password
              </label>

              <input
                id="currentPassword"
                type="password"
                value={passwordForm.currentPassword}
                onChange={(event) =>
                  setPasswordForm((previous) => ({
                    ...previous,
                    currentPassword: event.target.value,
                  }))
                }
                required
                disabled={passwordLoading}
              />
            </div>

            <div className="profile-form-grid">

              <div className="profile-form-field">
                <label htmlFor="newPassword">New Password</label>

                <input
                  id="newPassword"
                  type="password"
                  value={passwordForm.newPassword}
                  onChange={(event) =>
                    setPasswordForm((previous) => ({
                      ...previous,
                      newPassword: event.target.value,
                    }))
                  }
                  minLength={8}
                  required
                  disabled={passwordLoading}
                />
              </div>

              <div className="profile-form-field">
                <label htmlFor="confirmPassword">
                  Confirm New Password
                </label>

                <input
                  id="confirmPassword"
                  type="password"
                  value={passwordForm.confirmPassword}
                  onChange={(event) =>
                    setPasswordForm((previous) => ({
                      ...previous,
                      confirmPassword: event.target.value,
                    }))
                  }
                  minLength={8}
                  required
                  disabled={passwordLoading}
                />
              </div>

            </div>

            <div className="profile-form-actions">
              <button
                type="submit"
                className="primary-button"
                disabled={passwordLoading}
              >
                <LockKeyhole size={15} />
                {passwordLoading
                  ? "Updating..."
                  : "Update Password"}
              </button>
            </div>

          </form>
        </div>

      </div>
    </div>
  );
}

import { useContext, useState } from "react";
import { login } from "../authContext/apiCalls";
import { AuthContext } from "../authContext/AuthContext";
import { Link } from "react-router-dom";
import { FaLock, FaUser } from 'react-icons/fa';
import ReCAPTCHA from "react-google-recaptcha";
import { OAuthConfig } from "../../config/configuration";
import "./login.scss";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [recaptchaToken, setRecaptchaToken] = useState(null);
    const { dispatch } = useContext(AuthContext);
    const GOOGLE_CLIENT_KEY = process.env.REACT_APP_GOOGLE_CLIENT_KEY;
    

    const handleContinueWithGoogle = () => {
        const callbackUrl = process.env.REACT_APP_GATEWAY_REDIRECT_URI;
        const authUrl = OAuthConfig.authUri;
        const googleClientId = process.env.REACT_APP_CLIENT_ID;
    
        const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(
          callbackUrl
        )}&response_type=code&client_id=${googleClientId}&scope=openid%20email%20profile`;
    
        console.log(targetUrl);
    
        window.location.href = targetUrl;
    };
    


    const handleLogin = (e) => {
        e.preventDefault();
        if(!recaptchaToken) {
            alert("Please complete the CAPTCHA");
        }

        login({ username, password, captchaResponse: recaptchaToken }, dispatch);
        
    };

    const onCaptchaChange = (token) => {
        setRecaptchaToken(token);
      };
    return (
        <div className="login">
            <div className="body">
                <div className="wrapper">
                    <form action="">
                        <h1>Login</h1>
                        <div className="input-box">
                            <input
                                type="username"
                                placeholder="Username"
                                required
                                onChange={(e) => setUsername(e.target.value)}
                            />
                            <FaUser className="icon" />
                        </div>
                        <div className="input-box">
                            <input
                                type="password"
                                placeholder="Password"
                                required
                                onChange={(e) => setPassword(e.target.value)}
                            />
                            <FaLock className="icon" />
                        </div>
                        <div className="remember-forgot">
                            <label>
                                <input type="checkbox" />Remember me
                            </label>
                            <a href="#">
                                <Link to="/newPassword">
                                    Forgot Password?
                                </Link>
                            </a>
                        </div>
                        <div className="recaptcha-container">
                            <ReCAPTCHA
                                sitekey={GOOGLE_CLIENT_KEY}
                                onChange={onCaptchaChange}
                            />
                        </div>

                        <button type="submit" onClick={handleLogin}>
                            Login
                        </button>

                        <div className="register-link">
                            <p>Don't have an account ?
                                <a href="#">
                                    <Link to="/register">
                                        Register
                                    </Link>
                                    
                                </a>
                            </p>
                        </div>
                        <div className="register-link">
                            <p>Continue with Google ?
                                <a href="#" onClick={handleContinueWithGoogle}>
                                Login
                                </a>
                            </p>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

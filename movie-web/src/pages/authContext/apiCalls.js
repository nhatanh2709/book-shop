import axios from 'axios'
import { loginFailure, loginSuccess, loginStart,logoutSuccess } from './AuthActions'
import Toastify from 'toastify-js';
export const login = async(user, dispatch) => {
    dispatch(loginStart());
    try {
      const res = await axios.post(
        `${process.env.REACT_APP_GATEWAY_URL}/api/v1/identity/auth/token`,
        user,
        {
            headers: {
                "captcha-response": user.captchaResponse, 
            },
        }
      );
        console.log(res.data);
        dispatch(loginSuccess(res.data.result));
        Toastify({
            text: "Login Successfully",
            style: {
              background: "linear-gradient(to right, #00b09b, #96c93d)",
              display : "flex",
              justifyContent: "center",  // Căn giữa theo chiều ngang
              alignItems: "center",
            },
          }).showToast();
    } catch(err) {
        Toastify({
            text: err.response.data.message,
            style: {
              background: "red",
              display : "flex",
              justifyContent: "center",  // Căn giữa theo chiều ngang
              alignItems: "center",
            },
          }).showToast();
        dispatch(loginFailure());
    }
}

export const logout = async(dispatch) => {
    try {
        dispatch(logoutSuccess());
    } catch(err) {
        console.log(err);
    }
}

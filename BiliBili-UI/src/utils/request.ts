import axios from 'axios';

const service = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
});

service.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject(error);
    }
);

service.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        console.log('Error: ' + error);
        return Promise.reject(error);
    }
);

export default service; 
import React from 'react';
import {
    Button,
    Card,
    CardBody,
    Container,
    Form,
    FormGroup,
    Input,
    Label,
    Alert
} from "reactstrap";
import * as API_USERS from "./auth-api";
import {jwtDecode} from 'jwt-decode';

const backgroundStyle = {
    backgroundColor: '#f4f6f8',
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px'
};

const cardStyle = {
    width: '100%',
    maxWidth: '450px',
    border: 'none',
    borderRadius: '12px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.05)',
    overflow: 'hidden'
};

const inputStyle = {
    border: '1px solid #e2e8f0',
    backgroundColor: '#f8fafc',
    borderRadius: '8px',
    padding: '10px 15px',
    fontSize: '0.95rem',
    color: '#334155'
};

const labelStyle = {
    fontSize: '0.75rem',
    fontWeight: '600',
    textTransform: 'uppercase',
    letterSpacing: '0.5px',
    color: '#94a3b8',
    marginBottom: '5px'
};

const buttonStyle = {
    backgroundColor: '#1e293b',
    border: 'none',
    borderRadius: '8px',
    padding: '12px',
    fontWeight: '600',
    letterSpacing: '0.5px',
    marginTop: '20px',
    transition: 'all 0.3s ease'
};

const linkStyle = {
    color: '#2563eb',
    textDecoration: 'none',
    fontWeight: '600',
    cursor: 'pointer'
};

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            error: null,
            errorStatus: 0
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        let user = {
            username: this.state.username,
            password: this.state.password
        };

        API_USERS.login(user, (result, status, err) => {
            if (result !== null && (status === 200 || status === 201)) {
                const token = result.token ? result.token : result;
                localStorage.setItem("token", token);
                localStorage.setItem("username", this.state.username);

                if (result.id) {
                    localStorage.setItem("userId", result.id);
                }

                try {
                    jwtDecode(token);
                } catch (e) {
                    console.error(e);
                }

                window.location.href = "/";
            } else {
                this.setState({
                    errorStatus: status,
                    error: err
                });
            }
        });
    }

    render() {
        return (
            <div style={backgroundStyle}>
                <Container className="d-flex justify-content-center">
                    <Card style={cardStyle}>
                        <CardBody className="p-5">

                            {/* Header */}
                            <div className="text-center mb-5">
                                <h2 style={{ fontWeight: '700', color: '#1e293b', marginBottom: '10px' }}>
                                    Welcome Back
                                </h2>
                                <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
                                    Sign in to access your energy dashboard.
                                </p>
                            </div>

                            {/* Error Alert */}
                            {this.state.errorStatus > 0 &&
                                <Alert color="danger" style={{ borderRadius: '8px', border: 'none', fontSize: '0.9rem' }}>
                                    <i className="fa fa-exclamation-triangle mr-2"></i>
                                    Invalid username or password.
                                </Alert>
                            }

                            <Form onSubmit={this.handleSubmit}>
                                {/* Username */}
                                <FormGroup>
                                    <Label for="username" style={labelStyle}>Username</Label>
                                    <Input
                                        type="text"
                                        name="username"
                                        id="username"
                                        onChange={this.handleChange}
                                        required
                                        style={inputStyle}
                                    />
                                </FormGroup>

                                {/* Password */}
                                <FormGroup className="mb-4">
                                    <Label for="password" style={labelStyle}>Password</Label>
                                    <Input
                                        type="password"
                                        name="password"
                                        id="password"
                                        onChange={this.handleChange}
                                        required
                                        style={inputStyle}
                                    />
                                </FormGroup>

                                {/* Submit Button */}
                                <Button block style={buttonStyle} className="shadow-sm">
                                    Sign In
                                </Button>
                            </Form>

                            {/* Link */}
                            <div className="text-center mt-4">
                                <span style={{ color: '#64748b', fontSize: '0.9rem' }}>
                                    Don't have an account?
                                </span>
                                <a href="/register" style={{ ...linkStyle, marginLeft: '5px' }}>
                                    Sign up
                                </a>
                            </div>

                        </CardBody>
                    </Card>
                </Container>
            </div>
        );
    }
}

export default Login;
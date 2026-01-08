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
    Col,
    Row,
    Alert
} from "reactstrap";
import * as API_USERS from "./auth-api";

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
    maxWidth: '550px',
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

class Register extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            name: '',
            address: '',
            age: '',
            role: 'CLIENT',
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
            password: this.state.password,
            name: this.state.name,
            address: this.state.address,
            age: parseInt(this.state.age),
            role: this.state.role
        };

        API_USERS.register(user, (result, status, err) => {
            if (status === 200 || status === 201) {
                window.location.href = "/login";
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
                                    Create Account
                                </h2>
                            </div>

                            {/* Error Alert */}
                            {this.state.errorStatus > 0 &&
                                <Alert color="danger" style={{ borderRadius: '8px', border: 'none', fontSize: '0.9rem' }}>
                                    <i className="fa fa-exclamation-circle mr-2"></i>
                                    Registration failed. Please check your inputs.
                                </Alert>
                            }

                            <Form onSubmit={this.handleSubmit}>
                                {/* Full Name */}
                                <FormGroup>
                                    <Label for="name" style={labelStyle}>Full Name</Label>
                                    <Input
                                        name="name"
                                        id="name"
                                        onChange={this.handleChange}
                                        required
                                        style={inputStyle}
                                    />
                                </FormGroup>

                                {/* Username */}
                                <FormGroup>
                                    <Label for="username" style={labelStyle}>Username</Label>
                                    <Input
                                        name="username"
                                        id="username"
                                        onChange={this.handleChange}
                                        required
                                        style={inputStyle}
                                    />
                                </FormGroup>

                                {/* Password */}
                                <FormGroup>
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

                                {/* Address */}
                                <FormGroup>
                                    <Label for="address" style={labelStyle}>Address</Label>
                                    <Input
                                        name="address"
                                        id="address"
                                        onChange={this.handleChange}
                                        required
                                        style={inputStyle}
                                    />
                                </FormGroup>

                                {/* Age & Role Row */}
                                <Row form>
                                    <Col md={6}>
                                        <FormGroup>
                                            <Label for="age" style={labelStyle}>Age</Label>
                                            <Input
                                                type="number"
                                                name="age"
                                                id="age"
                                                onChange={this.handleChange}
                                                required
                                                style={inputStyle}
                                            />
                                        </FormGroup>
                                    </Col>
                                    <Col md={6}>
                                        <FormGroup>
                                            <Label for="role" style={labelStyle}>Role</Label>
                                            <Input
                                                type="select"
                                                name="role"
                                                id="role"
                                                onChange={this.handleChange}
                                                style={{...inputStyle, height: '45px'}}
                                            >
                                                <option value="CLIENT">Client</option>
                                                <option value="ADMIN">Admin</option>
                                            </Input>
                                        </FormGroup>
                                    </Col>
                                </Row>

                                {/* Submit Button */}
                                <Button block style={buttonStyle} className="shadow-sm">
                                    Sign Up
                                </Button>
                            </Form>

                            {/* Link */}
                            <div className="text-center mt-4">
                                <span style={{ color: '#64748b', fontSize: '0.9rem' }}>
                                    Already have an account?
                                </span>
                                <a href="/login" style={{ ...linkStyle, marginLeft: '5px' }}>
                                    Log in
                                </a>
                            </div>

                        </CardBody>
                    </Card>
                </Container>
            </div>
        );
    }
}

export default Register;
import React from 'react';
import {
    Container, Table, Card, CardBody, Button,
    Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input,
    Navbar, NavbarBrand, Nav, NavItem, Badge
} from 'reactstrap';
import * as API_USERS from "./api/users-api";

const backgroundStyle = {
    backgroundColor: '#f4f6f8',
    minHeight: '100vh',
    width: '100%',
    paddingBottom: '50px'
};

const navbarStyle = {
    backgroundColor: '#ffffff',
    boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',
    padding: '15px 0',
    marginBottom: '40px'
};

const cardStyle = {
    border: 'none',
    borderRadius: '12px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.05)',
    overflow: 'hidden',
    backgroundColor: '#ffffff'
};

const tableHeaderStyle = {
    backgroundColor: '#f8fafc',
    color: '#64748b',
    textTransform: 'uppercase',
    fontSize: '0.75rem',
    fontWeight: '700',
    letterSpacing: '0.5px',
    borderBottom: '2px solid #e2e8f0'
};

const inputStyle = {
    border: '1px solid #e2e8f0',
    backgroundColor: '#f8fafc',
    borderRadius: '8px',
    padding: '10px 15px',
    fontSize: '0.9rem',
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

const idBadgeStyle = {
    fontFamily: 'monospace',
    backgroundColor: '#f1f5f9',
    color: '#475569',
    padding: '4px 8px',
    borderRadius: '4px',
    fontSize: '0.85rem'
};

class UsersAdmin extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            users: [],
            currentUserId: localStorage.getItem("userId"),
            username: localStorage.getItem("username") || "Admin",

            isEditModalOpen: false,
            selectedUser: { id: '', name: '', age: '', address: '' },

            isDeleteModalOpen: false,
            userToDeleteId: null,

            error: null
        };

        this.toggleEditModal = this.toggleEditModal.bind(this);
        this.toggleDeleteModal = this.toggleDeleteModal.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.openDeleteConfirmation = this.openDeleteConfirmation.bind(this);
        this.executeDelete = this.executeDelete.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
    }

    componentDidMount() {
        this.fetchUsers();
    }

    handleLogout() {
        localStorage.clear();
        window.location.href = "/login";
    }

    fetchUsers() {
        API_USERS.getAllUsers((result, status, err) => {
            if (status === 200) {
                console.log("Users List:", result);
                this.setState({ users: result });
            } else {
                alert("Error loading users!");
            }
        });
    }

    toggleEditModal() {
        this.setState({ isEditModalOpen: !this.state.isEditModalOpen });
    }

    openEditModal(user) {
        this.setState({
            selectedUser: { ...user },
            isEditModalOpen: true
        });
    }

    toggleDeleteModal() {
        this.setState({ isDeleteModalOpen: !this.state.isDeleteModalOpen });
    }

    openDeleteConfirmation(id) {
        this.setState({
            userToDeleteId: id,
            isDeleteModalOpen: true
        });
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        this.setState(prevState => ({
            selectedUser: { ...prevState.selectedUser, [name]: value }
        }));
    }

    handleUpdate() {
        API_USERS.updateUser(this.state.selectedUser, (result, status, err) => {
            if (status === 200 || status === 204) {
                this.toggleEditModal();
                this.fetchUsers();
            } else {
                alert("Update failed: " + (err ? err.message : status));
            }
        });
    }

    executeDelete() {
        if (this.state.userToDeleteId) {
            API_USERS.deleteUser(this.state.userToDeleteId, (result, status, err) => {
                if (status === 200 || status === 204) {
                    this.toggleDeleteModal();
                    this.fetchUsers();
                } else {
                    alert("Delete failed!");
                    this.toggleDeleteModal();
                }
            });
        }
    }

    render() {
        return (
            <div style={backgroundStyle}>

                <Container>
                    {/* Header Pagina */}
                    <div className="mb-4">
                        <h2 style={{ fontWeight: '700', color: '#1e293b', marginBottom: '10px', marginTop: '20px' }}>User Management</h2>
                        <p style={{ color: '#64748b' }}>Manage user accounts, roles and permissions.</p>
                    </div>

                    {/* Tabel Utilizatori */}
                    <Card style={cardStyle}>
                        <CardBody className="p-0"> { }
                            <Table responsive hover className="mb-0" style={{ borderCollapse: 'collapse' }}>
                                <thead style={tableHeaderStyle}>
                                    <tr>
                                        <th className="p-4 border-0">User ID</th>
                                        <th className="p-4 border-0">Full Name</th>
                                        <th className="p-4 border-0">Age</th>
                                        <th className="p-4 border-0">Address</th>
                                        <th className="p-4 border-0">Role</th>
                                        <th className="p-4 border-0 text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {this.state.users.map(user => {
                                    const isClient = user.role === 'CLIENT';
                                    const isMe = this.state.currentUserId && (String(user.id) === String(this.state.currentUserId));
                                    const canEdit = isClient || isMe;
                                    const canDelete = isClient;

                                    return (
                                        <tr key={user.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                                            <td className="p-4">
                                                <span style={idBadgeStyle}>{user.id}</span>
                                            </td>
                                            <td className="p-4" style={{ fontWeight: '600', color: '#334155' }}>
                                                {user.name}
                                            </td>
                                            <td className="p-4" style={{ color: '#64748b' }}>
                                                {user.age}
                                            </td>
                                            <td className="p-4" style={{ color: '#64748b' }}>
                                                {user.address}
                                            </td>
                                            <td className="p-4">
                                                {user.role === 'ADMIN' ?
                                                    <Badge color="primary" className="px-3 py-2">ADMIN</Badge> :
                                                    <Badge color="light" className="text-dark px-3 py-2 border">CLIENT</Badge>
                                                }
                                            </td>
                                            <td className="p-4 text-right">
                                                {canEdit && (
                                                    <Button
                                                        color="link"
                                                        className="text-primary mr-2"
                                                        style={{ fontWeight: '600', textDecoration: 'none' }}
                                                        onClick={() => this.openEditModal(user)}
                                                    >
                                                        Edit
                                                    </Button>
                                                )}
                                                {canDelete && (
                                                    <Button
                                                        color="link"
                                                        className="text-danger"
                                                        style={{ fontWeight: '600', textDecoration: 'none' }}
                                                        onClick={() => this.openDeleteConfirmation(user.id)}
                                                    >
                                                        Delete
                                                    </Button>
                                                )}
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </Table>
                        </CardBody>
                    </Card>

                    {/* EDIT MODAL */}
                    <Modal isOpen={this.state.isEditModalOpen} toggle={this.toggleEditModal} centered>
                        <ModalHeader toggle={this.toggleEditModal} style={{ borderBottom: 'none', paddingLeft: '30px', paddingTop: '30px' }}>
                            <span style={{ fontWeight: '700', color: '#1e293b' }}>Edit User</span>
                        </ModalHeader>
                        <ModalBody style={{ padding: '30px' }}>
                            <Form>
                                <FormGroup>
                                    <Label style={labelStyle}>User ID</Label>
                                    <Input value={this.state.selectedUser.id} disabled style={{ ...inputStyle, backgroundColor: '#e2e8f0', opacity: 0.7 }} />
                                </FormGroup>
                                <FormGroup>
                                    <Label style={labelStyle}>Name</Label>
                                    <Input name="name" value={this.state.selectedUser.name || ''} onChange={this.handleInputChange} style={inputStyle} />
                                </FormGroup>
                                <FormGroup>
                                    <Label style={labelStyle}>Age</Label>
                                    <Input type="number" name="age" value={this.state.selectedUser.age || ''} onChange={this.handleInputChange} style={inputStyle} />
                                </FormGroup>
                                <FormGroup>
                                    <Label style={labelStyle}>Address</Label>
                                    <Input name="address" value={this.state.selectedUser.address || ''} onChange={this.handleInputChange} style={inputStyle} />
                                </FormGroup>
                            </Form>
                        </ModalBody>
                        <ModalFooter style={{ borderTop: 'none', paddingBottom: '30px', paddingRight: '30px' }}>
                            <Button color="light" onClick={this.toggleEditModal} style={{ marginRight: '10px', border: '1px solid #e2e8f0' }}>Cancel</Button>
                            <Button color="primary" onClick={this.handleUpdate} style={{ padding: '10px 20px' }}>Save Changes</Button>
                        </ModalFooter>
                    </Modal>

                    {/* DELETE MODAL */}
                    <Modal isOpen={this.state.isDeleteModalOpen} toggle={this.toggleDeleteModal} centered>
                        <ModalHeader toggle={this.toggleDeleteModal} style={{ borderBottom: 'none' }}>
                            <span style={{ color: '#ef4444', fontWeight: '700' }}>Confirm Deletion</span>
                        </ModalHeader>
                        <ModalBody>
                            <p style={{ fontSize: '1.1rem', color: '#1e293b' }}>Are you sure you want to delete this user?</p>
                            <p className="text-muted"><small>This action cannot be undone and will remove all associated data.</small></p>
                        </ModalBody>
                        <ModalFooter style={{ borderTop: 'none' }}>
                            <Button color="light" onClick={this.toggleDeleteModal} style={{ marginRight: '10px' }}>Cancel</Button>
                            <Button color="danger" onClick={this.executeDelete}>Delete User</Button>
                        </ModalFooter>
                    </Modal>
                </Container>
            </div>
        );
    }
}
export default UsersAdmin;
import React from 'react';
import {
    Container, Table, Card, CardBody, Button,
    Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input,
    Navbar, NavbarBrand, Nav, NavItem, Badge
} from 'reactstrap';
import * as API_DEVICES from "./api/devices-api";
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
    padding: '6px 10px',
    borderRadius: '4px',
    fontSize: '0.85rem',
    whiteSpace: 'nowrap',
    display: 'inline-block'
};


class DevicesAdmin extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            devices: [],
            users: [],
            username: localStorage.getItem("username") || "Admin",

            isEditModalOpen: false,
            modalType: 'ADD',
            currentDevice: { id: '', name: '', max_consumption: '', userId: '' },
            isAssignModalOpen: false,
            deviceToAssign: null,
            selectedUserIdForAssign: '',
            isUnassignModalOpen: false,
            deviceToUnassign: null,
            isDeleteModalOpen: false,
            deviceToDeleteId: null
        };

        this.toggleEditModal = this.toggleEditModal.bind(this);
        this.toggleAssignModal = this.toggleAssignModal.bind(this);
        this.toggleUnassignModal = this.toggleUnassignModal.bind(this);
        this.toggleDeleteModal = this.toggleDeleteModal.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSaveProperties = this.handleSaveProperties.bind(this);
        this.executeAssign = this.executeAssign.bind(this);
        this.executeUnassign = this.executeUnassign.bind(this);
        this.executeDelete = this.executeDelete.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
    }

    componentDidMount() {
        this.refreshData();
    }

    handleLogout() {
        localStorage.clear();
        window.location.href = "/login";
    }

    refreshData() {
        API_DEVICES.getAllDevices((devs, status) => {
            if (status === 200) this.setState({ devices: devs });
        });
        API_USERS.getAllUsers((usrs, status) => {
            if (status === 200) this.setState({ users: usrs });
        });
    }

    toggleEditModal() { this.setState({ isEditModalOpen: !this.state.isEditModalOpen }); }

    openAddModal() {
        this.setState({
            modalType: 'ADD',
            currentDevice: { id: '', name: '', max_consumption: '', userId: null },
            isEditModalOpen: true
        });
    }

    openEditModal(device) {
        this.setState({
            modalType: 'EDIT',
            currentDevice: { ...device },
            isEditModalOpen: true
        });
    }

    handleInputChange(event) {
        const { name, value } = event.target;
        this.setState(prevState => ({
            currentDevice: { ...prevState.currentDevice, [name]: value }
        }));
    }

    handleSaveProperties() {
        const dev = this.state.currentDevice;
        if (this.state.modalType === 'ADD') {
            API_DEVICES.createDevice(dev, (result, status) => {
                if (status === 200 || status === 201) {
                    this.toggleEditModal();
                    this.refreshData();
                } else alert("Error creating device!");
            });
        } else {
            API_DEVICES.updateDevice(dev, (result, status) => {
                if (status === 200 || status === 204) {
                    this.toggleEditModal();
                    this.refreshData();
                } else alert("Error updating device!");
            });
        }
    }

    toggleAssignModal() { this.setState({ isAssignModalOpen: !this.state.isAssignModalOpen }); }

    openAssignModal(device) {
        this.setState({
            deviceToAssign: device,
            selectedUserIdForAssign: '',
            isAssignModalOpen: true
        });
    }

    executeAssign() {
        if (!this.state.selectedUserIdForAssign) {
            alert("Please select a user!");
            return;
        }
        const updatedDevice = { ...this.state.deviceToAssign, userId: this.state.selectedUserIdForAssign };
        API_DEVICES.updateDevice(updatedDevice, (result, status) => {
            if (status === 200 || status === 204) {
                this.toggleAssignModal();
                this.refreshData();
            } else alert("Assignment failed!");
        });
    }

    toggleUnassignModal() { this.setState({ isUnassignModalOpen: !this.state.isUnassignModalOpen }); }

    openUnassignModal(device) {
        this.setState({ deviceToUnassign: device, isUnassignModalOpen: true });
    }

    executeUnassign() {
        const updatedDevice = { ...this.state.deviceToUnassign, userId: null };
        API_DEVICES.updateDevice(updatedDevice, (result, status) => {
            if (status === 200 || status === 204) {
                this.toggleUnassignModal();
                this.refreshData();
            } else alert("Unassignment failed!");
        });
    }

    toggleDeleteModal() { this.setState({ isDeleteModalOpen: !this.state.isDeleteModalOpen }); }

    openDeleteModal(id) {
        this.setState({ deviceToDeleteId: id, isDeleteModalOpen: true });
    }

    executeDelete() {
        API_DEVICES.deleteDevice(this.state.deviceToDeleteId, (result, status) => {
            if (status === 200 || status === 204) {
                this.toggleDeleteModal();
                this.refreshData();
            } else alert("Delete failed!");
        });
    }

    getUserName(userId) {
        if (!userId) return null;
        const u = this.state.users.find(user => String(user.id) === String(userId));
        return u ? u.name : "Unknown ID";
    }

    render() {
        return (
            <div style={backgroundStyle}>
                { }
                <Container fluid={true} style={{paddingLeft: '50px', paddingRight: '50px'}}>

                    {/* Header Pagina */}
                    <div className="d-flex justify-content-between align-items-end mb-4">
                        <div>
                            <h2 style={{ fontWeight: '700', color: '#1e293b', marginBottom: '10px', marginTop: '20px' }}>Device Management</h2>
                            <p style={{ color: '#64748b', margin: 0 }}>Create, assign and monitor smart devices.</p>
                        </div>
                        <Button color="primary" className="shadow-sm" onClick={() => this.openAddModal()} style={{ borderRadius: '8px', padding: '10px 20px', fontWeight: '600' }}>
                            <i className="fa fa-plus mr-2"></i> Add New Device
                        </Button>
                    </div>

                    {/* Tabel Dispozitive */}
                    <Card style={cardStyle}>
                        <CardBody className="p-0">
                            <Table responsive hover className="mb-0" style={{ borderCollapse: 'collapse' }}>
                                <thead style={tableHeaderStyle}>
                                    <tr>
                                        { }
                                        <th className="p-4 border-0">Device ID</th>
                                        <th className="p-4 border-0">Name / Description</th>
                                        <th className="p-4 border-0">Max Consumption</th>
                                        <th className="p-4 border-0">Allocation Status</th>
                                        <th className="p-4 border-0 text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {this.state.devices.map(device => {
                                    const assignedUser = this.getUserName(device.userId);

                                    return (
                                        <tr key={device.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                                            <td className="p-4">
                                                { }
                                                <span style={idBadgeStyle}>{device.id}</span>
                                            </td>
                                            <td className="p-4" style={{ fontWeight: '600', color: '#334155' }}>
                                                {device.name}
                                            </td>
                                            <td className="p-4">
                                                <span style={{ fontWeight: '700', color: '#2563eb' }}>{device.max_consumption}</span> <span className="text-muted">kWh</span>
                                            </td>
                                            <td className="p-4">
                                                <div className="d-flex align-items-center">
                                                    {assignedUser ? (
                                                        <>
                                                            <Badge color="success" className="mr-2 px-2 py-1">ASSIGNED</Badge>
                                                            <span style={{ fontSize: '0.9rem', color: '#475569', marginRight: '10px', whiteSpace: 'nowrap' }}>
                                                                to <strong>{assignedUser}</strong>
                                                            </span>
                                                            <Button color="warning" outline size="sm" style={{ fontSize: '0.7rem', padding: '2px 8px', borderRadius: '4px' }} onClick={() => this.openUnassignModal(device)}>Unassign</Button>
                                                        </>
                                                    ) : (
                                                        <>
                                                            <Badge color="secondary" className="mr-3 px-2 py-1">FREE</Badge>
                                                            <Button color="success" size="sm" style={{ fontSize: '0.75rem', padding: '4px 12px', borderRadius: '4px' }} onClick={() => this.openAssignModal(device)}>+ Assign</Button>
                                                        </>
                                                    )}
                                                </div>
                                            </td>
                                            <td className="p-4 text-right" style={{ whiteSpace: 'nowrap' }}>
                                                <Button color="link" className="text-primary mr-2" style={{ fontWeight: '600', textDecoration: 'none' }} onClick={() => this.openEditModal(device)}>Edit</Button>
                                                <Button color="link" className="text-danger" style={{ fontWeight: '600', textDecoration: 'none' }} onClick={() => this.openDeleteModal(device.id)}>Delete</Button>
                                            </td>
                                        </tr>
                                    );
                                })}
                                {this.state.devices.length === 0 && <tr><td colSpan="5" className="p-5 text-center text-muted">No devices found.</td></tr>}
                                </tbody>
                            </Table>
                        </CardBody>
                    </Card>

                    { }
                    {/* ADD/EDIT */}
                    <Modal isOpen={this.state.isEditModalOpen} toggle={this.toggleEditModal} centered>
                        <ModalHeader toggle={this.toggleEditModal} style={{ borderBottom: 'none', paddingLeft: '30px', paddingTop: '30px' }}>
                            <span style={{ fontWeight: '700', color: '#1e293b' }}>
                                {this.state.modalType === 'ADD' ? 'Add New Device' : 'Edit Device'}
                            </span>
                        </ModalHeader>
                        <ModalBody style={{ padding: '30px' }}>
                            <Form>
                                {this.state.modalType === 'EDIT' && (
                                    <FormGroup>
                                        <Label style={labelStyle}>Device ID</Label>
                                        <Input value={this.state.currentDevice.id} disabled style={{ ...inputStyle, backgroundColor: '#e2e8f0', opacity: 0.7 }} />
                                    </FormGroup>
                                )}
                                <FormGroup>
                                    <Label style={labelStyle}>Name</Label>
                                    <Input name="name" value={this.state.currentDevice.name} onChange={this.handleInputChange} style={inputStyle} />
                                </FormGroup>
                                <FormGroup>
                                    <Label style={labelStyle}>Max Hourly Consumption (kWh)</Label>
                                    <Input type="number" name="max_consumption" value={this.state.currentDevice.max_consumption} onChange={this.handleInputChange} style={inputStyle} />
                                </FormGroup>
                            </Form>
                        </ModalBody>
                        <ModalFooter style={{ borderTop: 'none', paddingBottom: '30px', paddingRight: '30px' }}>
                            <Button color="light" onClick={this.toggleEditModal} style={{ marginRight: '10px' }}>Cancel</Button>
                            <Button color="primary" onClick={this.handleSaveProperties}>Save Changes</Button>
                        </ModalFooter>
                    </Modal>

                    {/* ASSIGN */}
                    <Modal isOpen={this.state.isAssignModalOpen} toggle={this.toggleAssignModal} centered size="sm">
                        <ModalHeader toggle={this.toggleAssignModal} style={{ borderBottom: 'none' }}>
                            <span style={{ fontWeight: '700' }}>Assign Device</span>
                        </ModalHeader>
                        <ModalBody>
                            <Label style={labelStyle}>Select Client:</Label>
                            <Input type="select" value={this.state.selectedUserIdForAssign} onChange={(e) => this.setState({selectedUserIdForAssign: e.target.value})} style={inputStyle}>
                                <option value="">-- Choose User --</option>
                                {this.state.users.filter(u => u.role === 'CLIENT').map(u => (
                                    <option key={u.id} value={u.id}>{u.name} ({u.username})</option>
                                ))}
                            </Input>
                        </ModalBody>
                        <ModalFooter style={{ borderTop: 'none' }}>
                            <Button color="success" onClick={this.executeAssign} block>Confirm Assignment</Button>
                        </ModalFooter>
                    </Modal>

                    {/* UNASSIGN */}
                    <Modal isOpen={this.state.isUnassignModalOpen} toggle={this.toggleUnassignModal} centered>
                        <ModalHeader toggle={this.toggleUnassignModal} style={{ borderBottom: 'none' }}>
                            <span style={{ fontWeight: '700', color: '#f59e0b' }}>Unassign Device</span>
                        </ModalHeader>
                        <ModalBody>
                            <p>Are you sure you want to remove the assignment for this device?</p>
                        </ModalBody>
                        <ModalFooter style={{ borderTop: 'none' }}>
                            <Button color="light" onClick={this.toggleUnassignModal}>Cancel</Button>
                            <Button color="warning" onClick={this.executeUnassign}>Yes, Unassign</Button>
                        </ModalFooter>
                    </Modal>

                    {/* DELETE */}
                    <Modal isOpen={this.state.isDeleteModalOpen} toggle={this.toggleDeleteModal} centered>
                        <ModalHeader toggle={this.toggleDeleteModal} style={{ borderBottom: 'none' }}>
                            <span style={{ fontWeight: '700', color: '#ef4444' }}>Confirm Deletion</span>
                        </ModalHeader>
                        <ModalBody>
                            <p>Are you sure you want to delete this device?</p>
                        </ModalBody>
                        <ModalFooter style={{ borderTop: 'none' }}>
                            <Button color="light" onClick={this.toggleDeleteModal}>Cancel</Button>
                            <Button color="danger" onClick={this.executeDelete}>Delete Device</Button>
                        </ModalFooter>
                    </Modal>

                </Container>
            </div>
        );
    }
}
export default DevicesAdmin;
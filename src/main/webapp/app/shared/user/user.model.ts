export class User {
    public id?: string;
    public login?: string;
    public username?: string;
    public sex?: string;
    public email?: string;
    public status?: number;
    public phone?: string;
    public authorities?: any[];
    public password?: string;

    constructor(
        id?: any,
        login?: string,
        username?: string,
        sex?: string,
        email?: string,
        status?: number,
        phone?: string,
        authorities?: any[],
        is?: boolean,
        password?: string
    ) {
        this.id = id ? id : null;
        this.login = login ? login : null;
        this.username = username ? username : null;
        this.sex = sex ? sex : null;
        this.email = email ? email : null;
        this.status = status ? status : 0;
        this.phone = phone ? phone : null;
        this.authorities = authorities ? authorities : null;
        this.password = password ? password : null;
    }
}

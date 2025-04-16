# Projeto_Interdisciplinar_V_Back-End
## Todas as rotas começam desta forma
```http
  http://localhost:8080/api
```
Todos os null que retornam seram arrumados
### Observações úteis
- Placa e unica, então não é aceita duas iguais
- A exclusão de dados sao do tipo CASCADE, entao deletar usuario deleta tudo atrelado ao mesmo

Atençao FuelType é um enum

```java
public enum FuelType {
    GASOLINE,
    GASOLINE_PREMIUM,
    ETHANOL,
    DIESEL,
    ELECTRIC,
    GNV
}
```
## Usuários


### Rota de criação de Usuário

```http
  POST /users/create-user
```
Necessario o JSON da seguinte forma
```Json
{
  "email": "usuario@example.com",
  "username": "usuario123",
  "password": "senhaSegura123",
  "name": "João",
  "lastname": "Silva",
  "Phone": "+55 11 91234-5678"
}
```
Retorna o JSON da seguinte forma
```Json
{
  "id": "75cc378b-7d76-464e-906c-310595809e60",
  "fullName": "João Silva",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJsZW1icmFpIiwiaWF0IjoxNzQ0ODI1OTU0LCJleHAiOjE3NDQ4NjE5NTQsInN1YiI6InVzdWFyaW8xMjMifQ.Pi20dTixBOyfGK-fmNoFAjDq7unmk51RnHMQkadwbpM"
}
```

### Rota de atualização de Usuário

Sera implementada ainda


## Veiculos 

### Rota de criação de veiculo

```http
  POST /vehicle/create-vehicle/{idUsuario}
```
Necessario o JSON da seguinte forma
```Json
{
  "plate": "ABC45",
  "model": "Fiesta",
  "color": "Preto",
  "manufacturer": "Ford",
  "type": "Hatch",
  "description": "Carro de passeio",
  "year": "2015",
  "km": "50000",
  "fuelType": "GASOLINE",
  "fuelCapacity": 50.0,
  "fuelConsumption": 12.5
}
```
##### O banco de dados não aceita placa igual

### Rota de atualização de veiculo

```http
  PUT /vehicle/update-vehicle/{idVeiculo}
```
Necessario o JSON da seguinte forma
```Json
{
  "plate": "ABC45",
  "model": "Fiesta",
  "color": "Preto",
  "manufacturer": "Ford",
  "type": "Hatch",
  "description": "Carro de passeio",
  "year": "2015",
  "km": "50000",
  "fuelType": "GASOLINE",
  "fuelCapacity": 50.0,
  "fuelConsumption": 12.5
}
```

### Rota de deletar de veiculo

```http
  DELETE /vehicle/delete-vehicle/{idVeiculo}
```
Não há necessidade de Json

#### Rota de listagem de todos os Veiculos
```http
  GET /vehicle/listall-vehicle
```
Retornará JSON da seguinte forma
```Json
{
  "content": [
    {
      "id": null,
      "plate": "ABC45",
      "model": "Fiesta",
      "color": "Preto",
      "manufacturer": "Ford",
      "type": "Hatch",
      "description": "Carro de passeio",
      "year": "2015",
      "km": "50000.0",
      "fuelType": "GASOLINE",
      "fuelCapacity": 50.0,
      "fuelConsumption": 12.5,
      "userId": null
    },
    {
      "id": null,
      "plate": "ABC48",
      "model": "Fiesta",
      "color": "Preto",
      "manufacturer": "Ford",
      "type": "Hatch",
      "description": "Carro de passeio",
      "year": "2015",
      "km": "50000.0",
      "fuelType": "GNV",
      "fuelCapacity": 50.0,
      "fuelConsumption": 12.5,
      "userId": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}
```

## Posto de Combustivel

### Rota de criação de posto

```http
  POST /gasstation/create-gas-station
```
Necessario o JSON da seguinte forma
```Json
{
  "name": "posto012",
  "address":"rua 01554",
  "city":"São Paulo",
  "state":"São Paulo",
  "description":"funcionou",
  "rating": 5
}
```
### Rota de atualização de posto

```http
  PUT /gasstation/update-gas-station/{id}
```
Necessario o JSON da seguinte forma
```Json
{
  "plate": "ABC45",
  "model": "Fiesta",
  "color": "Preto",
  "manufacturer": "Ford",
  "type": "Hatch",
  "description": "Carro de passeio",
  "year": "2015",
  "km": "50000",
  "fuelType": "GASOLINE",
  "fuelCapacity": 50.0,
  "fuelConsumption": 12.5
}
```

### Rota de deletar de veiculo

```http
  DELETE /gasstation/delete-gas-station/{id}
```
Não há necessidade de Json

#### Rota de listagem de todos os Veiculos
```http
  GET /gasstation/listall-gas-station
```
Retornará JSON da seguinte forma
```Json
{
  "content": [
    {
      "id": "31db529e-5bd3-4f8d-bfab-7fc6e95fa8c2",
      "name": null,
      "address": null,
      "city": null,
      "state": null,
      "description": null,
      "rating": null
    },
    {
      "id": "3c86c64f-b139-43e1-a863-6efd6708d934",
      "name": null,
      "address": null,
      "city": null,
      "state": null,
      "description": null,
      "rating": null
    },
    {
      "id": "ee65abc4-497a-4618-9c5c-e8774952681e",
      "name": null,
      "address": null,
      "city": null,
      "state": null,
      "description": null,
      "rating": null
    },
    {
      "id": "c4805945-5910-4634-8550-7b726e9a5f3e",
      "name": null,
      "address": null,
      "city": null,
      "state": null,
      "description": null,
      "rating": null
    },
    {
      "id": "9e6888cd-f799-4bfd-8da4-996e58c22385",
      "name": "posto012",
      "address": "rua 01554",
      "city": "São Paulo",
      "state": "São Paulo",
      "description": "funcionou",
      "rating": 5
    },
    {
      "id": "4c149e7b-1647-4755-90eb-e97916bae5e4",
      "name": "posto01",
      "address": "rua 01554",
      "city": "São Paulo",
      "state": "São Paulo",
      "description": "funcionou",
      "rating": 5
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 6,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 6,
  "empty": false
}
```
#### Rota de listagem por id de posto
```http
  GET /gasstation/findbyid-gas-station/{id}
```
Retornará JSON da seguinte forma
```Json
{
  "id": "9e6888cd-f799-4bfd-8da4-996e58c22385",
  "name": "posto012",
  "address": "rua 01554",
  "city": "São Paulo",
  "state": "São Paulo",
  "description": "funcionou",
  "rating": 5
}
```

## Veiculos

### Rota de criação de veiculo

```http
  POST /vehicle/create-vehicle/{idUsuario}
```
Necessario o JSON da seguinte forma
```Json
{
  "plate": "ABC45",
  "model": "Fiesta",
  "color": "Preto",
  "manufacturer": "Ford",
  "type": "Hatch",
  "description": "Carro de passeio",
  "year": "2015",
  "km": "50000",
  "fuelType": "GASOLINE",
  "fuelCapacity": 50.0,
  "fuelConsumption": 12.5
}
```
##### O banco de dados não aceita placa igual

### Rota de atualização de veiculo

```http
  PUT /vehicle/update-vehicle/{idVeiculo}
```
Necessario o JSON da seguinte forma
```Json
{
  "plate": "ABC45",
  "model": "Fiesta",
  "color": "Preto",
  "manufacturer": "Ford",
  "type": "Hatch",
  "description": "Carro de passeio",
  "year": "2015",
  "km": "50000",
  "fuelType": "GASOLINE",
  "fuelCapacity": 50.0,
  "fuelConsumption": 12.5
}
```

### Rota de deletar de veiculo

```http
  DELETE /vehicle/delete-vehicle/{idVeiculo}
```
Não há necessidade de Json

#### Rota de listagem de todos os Veiculos
```http
  GET /vehicle/listall-vehicle
```
Retornará JSON da seguinte forma
```Json
{
  "content": [
    {
      "id": null,
      "plate": "ABC45",
      "model": "Fiesta",
      "color": "Preto",
      "manufacturer": "Ford",
      "type": "Hatch",
      "description": "Carro de passeio",
      "year": "2015",
      "km": "50000.0",
      "fuelType": "GASOLINE",
      "fuelCapacity": 50.0,
      "fuelConsumption": 12.5,
      "userId": null
    },
    {
      "id": null,
      "plate": "ABC48",
      "model": "Fiesta",
      "color": "Preto",
      "manufacturer": "Ford",
      "type": "Hatch",
      "description": "Carro de passeio",
      "year": "2015",
      "km": "50000.0",
      "fuelType": "GNV",
      "fuelCapacity": 50.0,
      "fuelConsumption": 12.5,
      "userId": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}
```
#### Rota de listagem por id de Veiculo
```http
  GET /vehicle/findbyid-vehicle/{id}
```
Retornará JSON da seguinte forma
```Json
{
  "uuid": "7b6c08af-8767-4bfe-a0d0-e7a012361f4d",
  "plate": "ABC48",
  "model": "Fiesta",
  "color": "Preto",
  "manufacturer": "Ford",
  "type": "Hatch",
  "description": "Carro de passeio",
  "year": "2015",
  "km": 50000.0,
  "fuelType": "GNV",
  "fuelCapacity": 50.0,
  "fuelConsumption": 12.5
}
```

## Abastecimento

### Rota de criação de novo abastecimento

```http
  POST /vehicle/fuel-refill/fuel-refill/new-fuel-refill/{idVeiculo}/{idPosto}
```
Necessario o JSON da seguinte forma
```Json
{
  "liters": 10.0,
  "pricePerLiter": 6.50,
  "kmAtRefill": 50005.0,
  "fuelType": "GASOLINE"
}
```
### Rota de atualização de abastecimento

```http
  PUT /vehicle/fuel-refill/update-fuel-refill/{id}
```
Necessario o JSON da seguinte forma
```Json
{
  "liters": 10.0,
  "pricePerLiter": 6.50,
  "kmAtRefill": 50005.0,
  "fuelType": "GASOLINE"
}
```

### Rota de deletar de abastecimento

```http
  DELETE /vehicle/fuel-refill/delete-refill/{id}
```
Não há necessidade de Json

#### Rota de listagem de todos os Abastecimentos
```http
  GET /vehicle/fuel-refill/list-all-fuel-refill
```
Retornará JSON da seguinte forma
```Json
{
  "content": [
    {
      "id": "de10e6d9-16ad-4943-85d9-6afe847deecf",
      "vehicleId": null,
      "stationId": null,
      "liters": 10.0,
      "pricePerLiter": 6.5,
      "totalCost": 65.0,
      "kmAtRefill": 50005.0,
      "fuelType": "GASOLINE",
      "refillDate": "2025-04-16"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```
#### Rota de listagem por id de Abastecimento
```http
  GET /vehicle/fuel-refill/find-by-id-fuel-refill/{id}
```
Retornará JSON da seguinte forma
```Json
{
  "id": "3cb76946-bb96-439d-9a80-e401b9bf416b",
  "vehicle": {
    "uuid": "4ad6b140-f6c3-49af-8fb5-da3280041465",
    "plate": "ABC4dsda",
    "model": "Fiesta",
    "color": "Preto",
    "manufacturer": "Ford",
    "type": "Hatch",
    "description": "Carro de passeio",
    "year": "2015",
    "km": 50005.0,
    "fuelType": "GNV",
    "fuelCapacity": 50.0,
    "fuelConsumption": 12.5
  },
  "station": {
    "id": "9e6888cd-f799-4bfd-8da4-996e58c22385",
    "name": "posto012",
    "address": "rua 01554",
    "city": "São Paulo",
    "state": "São Paulo",
    "description": "funcionou",
    "rating": 5
  },
  "liters": 10.0,
  "pricePerLiter": 6.5,
  "totalCost": 65.0,
  "kmAtRefill": 50005.0,
  "fuelType": "GASOLINE",
  "refillDate": "2025-04-16T15:26:43.626633"
}
```
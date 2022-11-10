package com.example.obspringsecurityjwt.rest;


import com.example.obspringsecurityjwt.domain.Car;
import com.example.obspringsecurityjwt.dto.CountDTO;
import com.example.obspringsecurityjwt.dto.MessageDTO;
import com.example.obspringsecurityjwt.service.CarService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class CarController {

    private final Logger log = LoggerFactory.getLogger(CarController.class);


    // dependencia
    private CarService carService;

    public CarController( CarService carService ) { // Spring inyecta la dependencia
        this.carService = carService;
    }

    /* ================== SPRING CRUD  METHODS ============= */

    /**
     * Buscar un coche por ID
     * http://localhost:8080/api/cars/1
     */
    @GetMapping("/cars/{id}")
    public ResponseEntity<Car> findById(@ApiParam("clave primaria car")@PathVariable Long id) {

        log.info("REST request to find one car ");
        Optional<Car> carOpt = this.carService.findById(id);

        // opción 1
        if (carOpt.isPresent())
            return ResponseEntity.ok(carOpt.get());

        return ResponseEntity.notFound().build();

        // opción 2
        /*return carOpt
                .map( car -> ResponseEntity.ok(car))
                .orElseGet()   () -> ResponseEntity.notFound().build()
        );*/
    }

/**
 * Buscar todos los coches en la base de datos
 * http://localhost:8080/api/cars
 */

@GetMapping("/cars")
public List<Car> findAll() {

    log.info("REST request to find all cars");

    return this.carService.findAll();

    }

    // Crear un coche
    @PostMapping("/cars")
    public ResponseEntity<Car> create (@RequestBody Car car ){

    log.info("REST request to create a new car");

    if(car.getId() != null  ) {// HAY ID - EL COCHE YA EXISTE NO PUEDO CREARLO DE NUEVO
        log.warn("Trying to create a new car with existent id ");
        return ResponseEntity.badRequest().build();
        }

    return ResponseEntity.ok(this.carService.save(car));

    }

    // Actualizar/modificar un coche
    @PutMapping("/cars")
    public ResponseEntity<Car> update(@RequestBody Car car ){

    log.info("REST request to update an existing car ");

    if (car.getId() == null) {  // NO HAY ID -POR TANTO NO EXISTE EL COCHE A ACTUALIZAR
        log.warn("Trying to update an existing car without id ");
        return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(this.carService.save(car));

    }

     // Eliminar un coche
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Car> delete(@PathVariable Long id) {

    log.info("REST request to delete an existing car");

    this.carService.deleteById(id);
    return ResponseEntity.noContent().build();

    }

    // Eliminar todos los coches
    @DeleteMapping("/cars")
    public ResponseEntity<Car> deleteAll() {

    log.info("REST request to delete all cars") ;

    this.carService.deleteAll();
    return ResponseEntity.noContent().build();
    }

    // Contar todos los coches
    @GetMapping("/cars/count")
    public ResponseEntity<CountDTO> count() {
    log.info("RESt request to count all cars");
    Long count = this.carService.count();
        CountDTO dto = new CountDTO(count);

    dto.setMessage("Que tenga usted un feliz dia :)");
    return ResponseEntity.ok(dto);
    }


    @GetMapping("/cars/hello")
    public ResponseEntity<String> hello( ) {

    return ResponseEntity.ok("Hello");

    }


    @GetMapping("/cars/hello2")
    public ResponseEntity<MessageDTO> hello2() {

    return ResponseEntity.ok(new MessageDTO("Hello"));

    }


    @GetMapping("/cars/deletemany/{ids}")
    public  ResponseEntity<Car> deleteMany(@PathVariable List<Long> ids){
    this.carService.deleteAllById(ids);

    return ResponseEntity.noContent().build();
    }

    /* ==================== CUSTOM CRUD METHODS ================= */

    // Buscar coches filtrando por fabricante y modelo
    @GetMapping("/cars/manufacturer/{manufacturer}/model/{model}")
        public List<Car> findByManufacturerAndModel(@PathVariable String manufacturer,
               @PathVariable String model)  {

            return this.carService.findByManufacturerAndModel(manufacturer, model);

        }

        // Buscar coches filtrando por número de puertas
        // @ApiIgnore
        @GetMapping("cars/doors/{doors}")
        @ApiOperation("buscar coches filtrando por numero puestas")
        public List<Car> findByDoors(@PathVariable Integer doors) {
        log.info("REST request to find cars by num doors");
        return this.carService.findByDoors(doors);
        }

        public List<Car> findByDoorsGreaterThanEqual(@PathVariable Integer doors) {

        return this.carService.findByDoorsGreaterThanEqual(doors);
        }

    }



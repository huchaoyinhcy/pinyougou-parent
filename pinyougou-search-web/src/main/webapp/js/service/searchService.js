app.service("serviceService", function ($http) {
    //搜索服务层
    this.search = function (searchMap) {
        return $http.post("itemsearch/search.do", searchMap)
    }
})